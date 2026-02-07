package com.oneself.gateway.filter;

import com.oneself.common.feature.security.model.bo.JwtSessionBO;
import com.oneself.common.infra.redis.model.enums.RedisKeyPrefixEnum;
import com.oneself.common.core.resp.Resp;
import com.oneself.common.core.utils.JacksonUtils;
import com.oneself.common.feature.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liuhuan
 * date 2025/12/24
 * packageName com.oneself.filter
 * className JwtAuthenticationGlobalFilter
 * description Gateway JWT 认证全局过滤器
 * version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 公开路径（不需要认证的路径）
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            // 认证服务公开接口
            "/oneself-auth/auth/login",
            "/oneself-auth/auth/captcha",
            "/oneself-auth/auth/refresh",
            // Swagger 文档路径（所有服务）
            "/oneself-auth/doc.html",
            "/oneself-auth/swagger-ui",
            "/oneself-auth/swagger-ui.html",
            "/oneself-auth/v3/api-docs",
            "/oneself-auth/v2/api-docs",
            "/oneself-auth/swagger-resources",
            "/oneself-system/doc.html",
            "/oneself-system/swagger-ui",
            "/oneself-system/swagger-ui.html",
            "/oneself-system/v3/api-docs",
            "/oneself-system/v2/api-docs",
            "/oneself-system/swagger-resources",
            "/oneself-demo/doc.html",
            "/oneself-demo/swagger-ui",
            "/oneself-demo/swagger-ui.html",
            "/oneself-demo/v3/api-docs",
            "/oneself-demo/v2/api-docs",
            "/oneself-demo/swagger-resources",
            "/oneself-quartz/doc.html",
            "/oneself-quartz/swagger-ui",
            "/oneself-quartz/swagger-ui.html",
            "/oneself-quartz/v3/api-docs",
            "/oneself-quartz/v2/api-docs",
            "/oneself-quartz/swagger-resources",
            "/oneself-ai/doc.html",
            "/oneself-ai/swagger-ui",
            "/oneself-ai/swagger-ui.html",
            "/oneself-ai/v3/api-docs",
            "/oneself-ai/v2/api-docs",
            "/oneself-ai/swagger-resources",
            // 静态资源
            "/oneself-auth/webjars",
            "/oneself-system/webjars",
            "/oneself-demo/webjars",
            "/oneself-quartz/webjars",
            "/oneself-ai/webjars",
            "/oneself-auth/favicon.ico",
            "/oneself-system/favicon.ico",
            "/oneself-demo/favicon.ico",
            "/oneself-quartz/favicon.ico",
            "/oneself-ai/favicon.ico"
    );

    /**
     * 会话滑动过期时长（小时）
     */
    private static final long SESSION_TIMEOUT_HOURS = 1;
    
    /**
     * 滑动续期阈值（分钟）
     */
    private static final long RENEW_THRESHOLD_MINUTES = 10;
    
    /**
     * 最大绝对过期天数
     */
    private static final long ABSOLUTE_EXPIRE_DAYS = 7;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "";

        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        // 检查是否为公开路径
        if (isPublicPath(path)) {
            log.debug("公开路径，跳过认证：{}", path);
            return chain.filter(exchange);
        }

        // 提取 Token
        String token = extractToken(request);
        
        if (StringUtils.isBlank(token)) {
            log.warn("请求未携带Token，path={}, ip={}", path, getClientIp(request));
            return unauthorizedResponse(exchange.getResponse(), "未认证，请登录");
        }

        try {
            // 解析并验证 Token
            log.debug("开始验证Token，path={}, ip={}", path, getClientIp(request));
            JwtSessionBO sessionBO = parseAndValidateToken(token);
            
            if (sessionBO == null) {
                log.warn("Token验证失败：会话无效，path={}, ip={}, token={}", 
                        path, getClientIp(request), token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");
                return unauthorizedResponse(exchange.getResponse(), "Token 无效或已过期");
            }

            // 从 Redis 获取用户会话信息
            String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionBO.getSessionId();
            String sessionJson = redisTemplate.opsForValue().get(redisKey);

            if (StringUtils.isBlank(sessionJson)) {
                log.warn("Token验证失败：会话不存在，sessionId={}, path={}, ip={}",
                        sessionBO.getSessionId(), path, getClientIp(request));
                return unauthorizedResponse(exchange.getResponse(), "会话已过期，请重新登录");
            }

            // 将用户信息添加到请求头，传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", sessionBO.getUserId())
                    .header("X-Username", sessionBO.getUsername())
                    .header("X-Session-Id", sessionBO.getSessionId())
                    .build();

            log.info("Token验证成功，准备转发请求，userId={}, username={}, path={}, ip={}, targetService=oneself-system",
                    sessionBO.getUserId(), sessionBO.getUsername(), path, getClientIp(request));

            return chain.filter(exchange.mutate().request(modifiedRequest).build())
                    .doOnSuccess(v -> {
                        log.debug("请求转发成功，path={}", path);
                    })
                    .doOnError(e -> {
                        log.error("请求转发失败，path={}, error={}", path, e.getMessage(), e);
                    });

        } catch (Exception e) {
            log.error("Token鉴权异常，path={}, ip={}, error={}", path, getClientIp(request), e.getMessage(), e);
            return unauthorizedResponse(exchange.getResponse(), "Token 解析失败");
        }
    }

    /**
     * 检查是否为公开路径
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 从请求头提取 Token
     */
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(authHeader)) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    /**
     * 解析并验证 Token
     */
    private JwtSessionBO parseAndValidateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        try {
            // 解析 JWT
            log.debug("开始解析JWT Token");
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            log.debug("JWT解析成功，subject={}", subject);
            
            JwtSessionBO sessionBO = JacksonUtils.fromJson(subject, JwtSessionBO.class);

            if (sessionBO == null) {
                log.warn("JWT subject 解析为 null");
                return null;
            }

            String sessionId = sessionBO.getSessionId();
            String userId = sessionBO.getUserId();
            String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
            String userKey = RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;

            log.debug("检查Redis会话，sessionId={}, userId={}, redisKey={}", sessionId, userId, redisKey);

            // 清理已过期 session（ZSet 中的老数据）
            redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

            // 检查 Redis 会话是否存在
            Long expire = redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            log.debug("Redis会话过期时间，redisKey={}, expire={}ms", redisKey, expire);
            
            if (expire <= 0) {
                // 会话已过期，清理 ZSet
                log.warn("Redis会话不存在或已过期，sessionId={}, userId={}", sessionId, userId);
                redisTemplate.opsForZSet().remove(userKey, sessionId);
                return null;
            }

            // 绝对过期检查
            long absoluteExpireMillis = TimeUnit.DAYS.toMillis(ABSOLUTE_EXPIRE_DAYS);
            long loginTime = sessionBO.getLoginTime();
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - loginTime;
            
            log.debug("绝对过期检查，loginTime={}, currentTime={}, elapsedTime={}ms, absoluteExpire={}ms", 
                    loginTime, currentTime, elapsedTime, absoluteExpireMillis);
            
            if (elapsedTime > absoluteExpireMillis) {
                redisTemplate.delete(redisKey);
                redisTemplate.opsForZSet().remove(userKey, sessionId);
                log.warn("用户 [{}] 的 session [{}] 已超过 {} 天，强制过期", userId, sessionId, ABSOLUTE_EXPIRE_DAYS);
                return null;
            }

            // 滑动过期：必要时续期
            refreshSessionIfNecessary(userId, sessionId, redisKey, userKey);

            log.debug("Token验证成功，sessionId={}, userId={}, username={}", 
                    sessionId, userId, sessionBO.getUsername());
            return sessionBO;

        } catch (Exception e) {
            log.error("Token解析失败，error={}, message={}", e.getClass().getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 滑动续期：如果剩余时间小于阈值，则续期
     */
    private void refreshSessionIfNecessary(String userId, String sessionId, String redisKey, String userKey) {
        Long ttlMinutes = redisTemplate.getExpire(redisKey, TimeUnit.MINUTES);
        if (ttlMinutes > 0 && ttlMinutes < RENEW_THRESHOLD_MINUTES) {
            redisTemplate.expire(redisKey, SESSION_TIMEOUT_HOURS, TimeUnit.HOURS);
            redisTemplate.expire(userKey, SESSION_TIMEOUT_HOURS, TimeUnit.HOURS);

            long newExpireAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(SESSION_TIMEOUT_HOURS);
            redisTemplate.opsForZSet().add(userKey, sessionId, newExpireAt);
            log.debug("用户 [{}] 的 session [{}] 已滑动续期 {} 小时", userId, sessionId, SESSION_TIMEOUT_HOURS);
        }
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Resp<String> resp = Resp.failure(message, HttpStatus.UNAUTHORIZED);
        String json = JacksonUtils.toJsonString(resp);
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
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
            ip = request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        // 处理多个IP的情况（取第一个）
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    public int getOrder() {
        // 设置过滤器执行顺序，数字越小优先级越高
        return -100;
    }
}

