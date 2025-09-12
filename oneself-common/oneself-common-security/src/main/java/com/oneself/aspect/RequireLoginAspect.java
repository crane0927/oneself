package com.oneself.aspect;

import com.oneself.annotation.RequireLogin;
import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


/**
 * @author liuhuan
 * date 2024/7/3
 * packageName com.py.aspect
 * className RequireLoginAspect
 * description 是否需要登录的切面
 * version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequireLoginAspect {

    private final HttpServletRequest request;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 拦截所有标注了 @RequireLogin 的方法
     */
    @Around("@annotation(requireLogin) || @within(requireLogin)")
    public Object checkLogin(ProceedingJoinPoint joinPoint, RequireLogin requireLogin) throws Throwable {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            throw new OneselfException("缺少认证 token");
        }

        try {
            // 解析 token
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            LoginUserSessionBO sessionBO = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

            String sessionId = sessionBO.getSessionId();
            String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;

            String redisVal = redisTemplate.opsForValue().get(redisKey);

            if (StringUtils.isBlank(redisVal)) {
                throw new OneselfException("登录已过期，请重新登录");
            }

            log.debug("用户 [{}] 已登录，sessionId={}", sessionBO.getUsername(), sessionId);
        } catch (Exception e) {
            if (requireLogin.strict()) {
                throw new OneselfException("token 无效或用户未登录");
            } else {
                log.warn("非严格模式下放行：{}", e.getMessage());
            }
        }

        return joinPoint.proceed();
    }
}