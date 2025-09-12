package com.oneself.utils;

import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author liuhuan
 * date 2025/9/12
 * packageName com.oneself.utils
 * className SecurityUtils
 * description
 * version 1.0
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final HttpServletRequest request;
    private final RedisTemplate<String, String> redisTemplate;

    private static final ThreadLocal<LoginUserSessionBO> USER_HOLDER = new ThreadLocal<>();
    private static final String DEFAULT_USER = "system";

    /**
     * 从请求头获取 token 并解析
     */
    public String resolveToken() {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    /**
     * 解析 token 并校验 Redis session
     */
    public LoginUserSessionBO parseAndValidateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        try {
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            LoginUserSessionBO sessionBO = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

            String redisKey = RedisKeyPrefixEnum.SYSTEM_NAME.getPrefix()
                    + RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix()
                    + sessionBO.getSessionId();

            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                USER_HOLDER.set(sessionBO); // 保存到 ThreadLocal
                return sessionBO;
            }
        } catch (Exception ignored) {
            // token 无效或者解析失败
        }
        return null;
    }

    /**
     * 获取当前登录用户
     */
    public LoginUserSessionBO getCurrentUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前登录用户名，失败返回默认用户
     */
    public String getCurrentUsername() {
        LoginUserSessionBO user = getCurrentUser();
        return user != null ? user.getUsername() : DEFAULT_USER;
    }

    /**
     * 清理 ThreadLocal
     */
    public void clear() {
        USER_HOLDER.remove();
    }
}
