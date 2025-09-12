package com.oneself.filter;

import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserBO;
import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.JwtUtils;
import io.jsonwebtoken.Claims;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (StringUtils.isNotBlank(token)) {
            try {
                // 1. 解析 JWT
                Claims claims = JwtUtils.parseJWT(token);
                String subject = claims.getSubject();
                LoginUserSessionBO sessionBO = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

                String sessionId = sessionBO.getSessionId();
                String userId = sessionBO.getUserId();

                // 2. 校验 Redis String 会话
                String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
                String sessionJson = redisTemplate.opsForValue().get(redisKey);

                // 3. 清理用户 SortedSet 中过期 sessionId
                String userKey = RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;
                redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

                // 4. 如果当前 session 已过期，顺手删除 SortedSet 中的 sessionId
                if (StringUtils.isBlank(sessionJson)) {
                    redisTemplate.opsForZSet().remove(userKey, sessionId);
                    throw new OneselfException("登录已过期或 sessionId 无效");
                }

                // 5. 反序列化 Redis 中的用户信息，设置 Spring Security 上下文
                LoginUserBO loginUser = JacksonUtils.fromJson(sessionJson, LoginUserBO.class);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.error("Token 鉴权失败", e);
                throw new OneselfException("token 无效或解析失败", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}