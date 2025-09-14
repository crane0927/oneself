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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
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
@Order(1) // 切面优先执行
public class RequireAuthAspect {

    private final SecurityUtils securityUtils;

    private static final String POINTCUT_SIGN =
            "@annotation(com.oneself.annotation.RequireLogin) || " +
                    "@annotation(com.oneself.annotation.RequireRoles) || " +
                    "@annotation(com.oneself.annotation.RequirePermission)";

    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 1. 尝试获取当前用户
            LoginUserSessionBO user = securityUtils.getCurrentUser();

            // 2. 如果 ThreadLocal 为空，则解析 token
            if (user == null) {
                String token = securityUtils.resolveToken();
                user = securityUtils.parseAndValidateToken(token);
                securityUtils.setCurrentUser(user);
            }

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 3. 检查 @RequireLogin
            RequireLogin requireLogin = method.getAnnotation(RequireLogin.class);
            if (requireLogin != null && requireLogin.strict() && user == null) {
                throw new OneselfException("token 无效或用户未登录");
            }

            // 4. 检查 @RequireRoles
            RequireRoles requireRoles = method.getAnnotation(RequireRoles.class);
            if (requireRoles != null && user != null) {
                securityUtils.checkRole(requireRoles.value(), requireRoles.strict());
            }

            // 5. 检查 @RequirePermission
            RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
            if (requirePermission != null && user != null) {
                securityUtils.checkPermission(requirePermission.value(), requirePermission.strict());
            }

            return joinPoint.proceed();
        } finally {
            // 请求结束时清理 ThreadLocal
            securityUtils.clear();
        }
    }
}