package com.oneself.aspect;

import com.oneself.annotation.RequireLogin;
import com.oneself.annotation.RequirePermission;
import com.oneself.annotation.RequireRoles;
import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
public class RequireAuthAspect {

    private final SecurityUtils securityUtils;

    private static final String POINTCUT_SIGN =
            "@annotation(com.oneself.annotation.RequireLogin) || " +
                    "@annotation(com.oneself.annotation.RequireRoles) || " +
                    "@annotation(com.oneself.annotation.RequirePermission)";

    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {}

    @Around("pointcut() && @annotation(requireLogin)")
    public Object aroundLogin(ProceedingJoinPoint joinPoint, RequireLogin requireLogin) throws Throwable {
        try {
            String token = securityUtils.resolveToken();
            LoginUserSessionBO user = securityUtils.parseAndValidateToken(token);
            if (user == null && requireLogin.strict()) {
                throw new OneselfException("token 无效或用户未登录");
            }
            return joinPoint.proceed();
        } finally {
            securityUtils.clear();
        }
    }

    @Around("pointcut() && @annotation(requireRoles)")
    public Object aroundRoles(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        try {
            securityUtils.checkRole(requireRoles.value());
            return joinPoint.proceed();
        } finally {
            securityUtils.clear();
        }
    }

    @Around("pointcut() && @annotation(requirePermission)")
    public Object aroundPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        try {
            securityUtils.checkPermission(requirePermission.value());
            return joinPoint.proceed();
        } finally {
            securityUtils.clear();
        }
    }
}