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
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    public JwtAuthenticationTokenFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头获取 Token
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

                // 2. 校验 Redis 是否有此 session
                String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
                String sessionJson = redisTemplate.opsForValue().get(redisKey);

                if (StringUtils.isBlank(sessionJson)) {
                    throw new OneselfException("登录已过期或 sessionId 无效");
                }

                // 3. 反序列化 Redis 中的用户信息
                LoginUserBO loginUser = JacksonUtils.fromJson(sessionJson, LoginUserBO.class);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                throw new OneselfException("token 无效或解析失败", e);
            }
        }

        // 放行
        filterChain.doFilter(request, response);
    }
}