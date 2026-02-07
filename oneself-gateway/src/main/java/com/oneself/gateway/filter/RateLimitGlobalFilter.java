package com.oneself.gateway.filter;

import com.oneself.common.core.resp.Resp;
import com.oneself.common.core.utils.JacksonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.filter
 * className RateLimitGlobalFilter
 * description Gateway 限流全局过滤器（基于 Redis + Lua 脚本）
 * version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitGlobalFilter implements GlobalFilter, Ordered {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 限流配置（从配置文件读取）
     */
    @Value("${gateway.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${gateway.rate-limit.default-limit:100}")
    private int defaultRateLimit;

    @Value("${gateway.rate-limit.window-seconds:60}")
    private int rateLimitWindowSeconds;

    private static final String RATE_LIMIT_KEY_PREFIX = "gateway:rate-limit:";
    private static final String LUA_SCRIPT_PATH = "lua/rate-limit.lua";

    /**
     * Lua 脚本内容（从资源文件加载）
     */
    private String rateLimitLuaScript;

    /**
     * 初始化时加载 Lua 脚本
     */
    @PostConstruct
    public void init() {
        try {
            Resource resource = new ClassPathResource(LUA_SCRIPT_PATH);
            rateLimitLuaScript = StreamUtils.copyToString(
                    resource.getInputStream(), 
                    StandardCharsets.UTF_8
            );
            log.info("Lua 脚本加载成功：{}", LUA_SCRIPT_PATH);
        } catch (IOException e) {
            log.error("加载 Lua 脚本失败：{}", LUA_SCRIPT_PATH, e);
            throw new RuntimeException("无法加载限流 Lua 脚本", e);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果限流未启用，直接放行
        if (!rateLimitEnabled) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 跳过公开路径的限流
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // 获取限流 key（基于 IP + 路径）
        String clientIp = getClientIp(request);
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + clientIp + ":" + path;

        // 执行限流检查
        return checkRateLimit(rateLimitKey)
                .flatMap(allowed -> {
                    if (allowed) {
                        return chain.filter(exchange);
                    } else {
                        log.warn("请求被限流，ip={}, path={}", clientIp, path);
                        return rateLimitResponse(exchange.getResponse());
                    }
                });
    }

    /**
     * 检查是否允许请求（使用 Lua 脚本保证原子性）
     */
    private Mono<Boolean> checkRateLimit(String key) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(rateLimitLuaScript);
        script.setResultType(Long.class);

        long now = System.currentTimeMillis() / 1000;
        List<String> keys = Collections.singletonList(key);
        Object[] args = {
            String.valueOf(rateLimitWindowSeconds),
            String.valueOf(defaultRateLimit),
            String.valueOf(now)
        };

        try {
            Long result = redisTemplate.execute(script, keys, args);
            return Mono.just(result == 1);
        } catch (Exception e) {
            log.error("限流检查异常，key={}, error={}", key, e.getMessage(), e);
            // 异常时允许请求通过，避免限流功能影响正常业务
            return Mono.just(true);
        }
    }

    /**
     * 返回限流响应
     */
    private Mono<Void> rateLimitResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Resp<String> resp = Resp.failure("请求过于频繁，请稍后再试", HttpStatus.TOO_MANY_REQUESTS);
        String json = JacksonUtils.toJsonString(resp);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 判断是否是公开路径（不需要限流）
     */
    private boolean isPublicPath(String path) {
        // 登录、验证码等公开接口不需要限流
        return path.startsWith("/oneself-auth/auth/login") ||
               path.startsWith("/oneself-auth/auth/captcha") ||
               path.startsWith("/actuator"); // Actuator 端点
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            if (request.getRemoteAddress() != null) {
                ip = request.getRemoteAddress().getAddress().getHostAddress();
            }
        }
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return StringUtils.isNotBlank(ip) ? ip : "unknown";
    }

    @Override
    public int getOrder() {
        return -200; // 在 JWT 过滤器之前执行
    }
}

