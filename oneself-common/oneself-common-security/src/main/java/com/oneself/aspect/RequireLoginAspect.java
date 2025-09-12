package com.oneself.aspect;

import com.oneself.annotation.RequireLogin;
import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private final SecurityUtils securityUtils;

    @Around("@annotation(requireLogin) || @within(requireLogin)")
    public Object checkLogin(ProceedingJoinPoint joinPoint, RequireLogin requireLogin) throws Throwable {
        try {
            // 从请求头获取 token
            String token = securityUtils.resolveToken();
            LoginUserSessionBO user = securityUtils.parseAndValidateToken(token);

            if (user == null && requireLogin.strict()) {
                throw new OneselfException("token 无效或用户未登录");
            }

            if (user != null) {
                log.debug("用户 [{}] 已登录，sessionId={}", user.getUsername(), user.getSessionId());
            } else {
                log.warn("非严格模式下放行：未登录用户访问");
            }

            return joinPoint.proceed();
        } finally {
            securityUtils.clear(); // 防止内存泄漏
        }
    }
}