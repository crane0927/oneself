package com.oneself.filter;

import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserBO;
import com.oneself.model.bo.LoginUserSessionBO;
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

        if (StringUtils.isNotBlank(token)) {
            try {
                // 使用 SecurityUtils 解析 token 并校验 Redis（含滑动+绝对过期）
                LoginUserSessionBO sessionBO = securityUtils.parseAndValidateToken(token);

                if (sessionBO != null) {
                    // 从 Redis 中获取完整用户信息，设置 Spring Security 上下文
                    String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionBO.getSessionId();
                    String sessionJson = redisTemplate.opsForValue().get(redisKey);

                    if (StringUtils.isNotBlank(sessionJson)) {
                        LoginUserBO loginUser = JacksonUtils.fromJson(sessionJson, LoginUserBO.class);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                log.error("Token 鉴权失败", e);
                throw new OneselfException("token 无效或解析失败", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}