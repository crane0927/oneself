package com.oneself.filter;

import com.oneself.exception.OneselfException;
import com.oneself.model.bo.JwtSessionBO;
import com.oneself.model.bo.LoginUserBO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.filter
 * className JwtAuthenticationTokenFilter
 * description JWT 认证过滤器
 * version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    private final SecurityUtils securityUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = securityUtils.resolveToken();
        String requestUri = request.getRequestURI();
        String ip = getClientIp(request);

        if (StringUtils.isNotBlank(token)) {
            try {
                // 使用 SecurityUtils 解析 token 并校验 Redis（含滑动+绝对过期）
                JwtSessionBO sessionBO = securityUtils.parseAndValidateToken(token);

                if (sessionBO != null) {
                    // 从 Redis 中获取完整用户信息，设置 Spring Security 上下文
                    String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionBO.getSessionId();
                    String sessionJson = redisTemplate.opsForValue().get(redisKey);

                    if (StringUtils.isNotBlank(sessionJson)) {
                        LoginUserBO loginUser = JacksonUtils.fromJson(sessionJson, LoginUserBO.class);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Token验证成功，userId={}, username={}, uri={}, ip={}",
                                loginUser.getId(), loginUser.getUsername(), requestUri, ip);
                    } else {
                        log.warn("Token验证失败：会话不存在，sessionId={}, uri={}, ip={}",
                                sessionBO.getSessionId(), requestUri, ip);
                    }
                } else {
                    log.debug("Token验证失败：sessionBO为空，uri={}, ip={}", requestUri, ip);
                }
            } catch (Exception e) {
                log.warn("Token鉴权异常，uri={}, ip={}, error={}", requestUri, ip, e.getMessage());
                throw new OneselfException("Token 无效或解析失败", e);
            }
        } else {
            log.trace("请求未携带Token，uri={}, ip={}", requestUri, ip);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况（取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}