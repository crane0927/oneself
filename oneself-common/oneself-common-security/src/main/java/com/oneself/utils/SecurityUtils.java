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

    private static final String USER = "system";

    /**
     * 获取当前登录用户名
     */
    public String getCurrentUsername() {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            return USER;
        }

        try {
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            LoginUserSessionBO sessionBO = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

            // 校验 redis session 是否存在
            String redisKey =
                    RedisKeyPrefixEnum.SYSTEM_NAME.getPrefix() + RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionBO.getSessionId();
            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                return USER;
            }

            return sessionBO.getUsername();
        } catch (Exception e) {
            return USER;
        }
    }
}
