package com.oneself.aspect;

import com.oneself.annotation.RequireLogin;
import com.oneself.annotation.RequirePermission;
import com.oneself.annotation.RequireRoles;
import com.oneself.exception.OneselfException;
import com.oneself.model.bo.JwtSessionBO;
import com.oneself.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
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
                    "@within(com.oneself.annotation.RequireLogin) || " +
                    "@annotation(com.oneself.annotation.RequireRoles) || " +
                    "@within(com.oneself.annotation.RequireRoles) || " +
                    "@annotation(com.oneself.annotation.RequirePermission) || " +
                    "@within(com.oneself.annotation.RequirePermission)";

    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();

        // 1. 尝试获取当前用户（Filter 应该已经设置了）
        JwtSessionBO user = securityUtils.getCurrentUser();

        // 2. 如果 ThreadLocal 为空，则尝试解析 token（兜底逻辑）
        if (user == null) {
            String token = securityUtils.resolveToken();
            if (StringUtils.isNotBlank(token)) {
                user = securityUtils.parseAndValidateToken(token);
                if (user != null) {
                    securityUtils.setCurrentUser(user);
                }
            }
        }

        // -------------------- 处理注解 --------------------

        // 3. 获取 @RequireLogin（方法优先，方法没有再查类）
        RequireLogin requireLogin = AnnotationUtils.findAnnotation(method, RequireLogin.class);
        if (requireLogin == null) {
            requireLogin = AnnotationUtils.findAnnotation(targetClass, RequireLogin.class);
        }

        // 4. 校验登录
        if (requireLogin != null && requireLogin.strict() && user == null) {
            throw new OneselfException("Token 无效或用户未登录");
        }

        // 5. 获取 @RequireRoles（方法优先，方法没有再查类）
        RequireRoles requireRoles = AnnotationUtils.findAnnotation(method, RequireRoles.class);
        if (requireRoles == null) {
            requireRoles = AnnotationUtils.findAnnotation(targetClass, RequireRoles.class);
        }
        // 如果有角色注解，必须登录
        if (requireRoles != null) {
            if (user == null) {
                throw new OneselfException("未登录用户禁止访问此接口（角色校验）");
            }
            securityUtils.checkRole(requireRoles.value(), requireRoles.strict());
        }

        // 6. 获取 @RequirePermission（方法优先，方法没有再查类）
        RequirePermission requirePermission = AnnotationUtils.findAnnotation(method, RequirePermission.class);
        if (requirePermission == null) {
            requirePermission = AnnotationUtils.findAnnotation(targetClass, RequirePermission.class);
        }
        // 如果有权限注解，必须登录
        if (requirePermission != null) {
            if (user == null) {
                throw new OneselfException("未登录用户禁止访问此接口（权限校验）");
            }
            securityUtils.checkPermission(requirePermission.value(), requirePermission.strict());
        }

        // 7. 执行目标方法
        // 注意：ThreadLocal 的清理由 SecurityContextFilter 统一管理，不需要在这里清理
        return joinPoint.proceed();
    }
}