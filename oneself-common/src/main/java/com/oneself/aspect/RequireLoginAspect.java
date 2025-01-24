package com.oneself.aspect;

import com.oneself.annotation.RequireLogin;
import com.oneself.exception.OneselfLoginException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * @author liuhuan
 * date 2024/7/3
 * packageName com.py.aspect
 * className RequireLoginAspect
 * description 是否需要登录的切面
 * version 1.0
 */
@Aspect
@Component
public class RequireLoginAspect {

    @Before("@within(requireLogin) || @annotation(requireLogin)")
    public void checkLoginStatus(JoinPoint joinPoint, RequireLogin requireLogin) throws Throwable {
        if (requireLogin == null) {
            requireLogin = getRequireLoginAnnotation(joinPoint);
        }
        if (requireLogin != null && requireLogin.strict()) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
            HttpServletRequest request = attributes.getRequest();

            String token = request.getHeader("user-token");
            if (!isValidToken(token)) {
                throw new OneselfLoginException("请先登录");
            }
        }
    }

    private RequireLogin getRequireLoginAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 优先获取方法上的注解
        if (method.isAnnotationPresent(RequireLogin.class)) {
            return method.getAnnotation(RequireLogin.class);
        }

        // 获取类上的注解
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(RequireLogin.class)) {
            return targetClass.getAnnotation(RequireLogin.class);
        }

        return null;
    }

    private boolean isValidToken(String token) {
        // 实际的令牌验证逻辑，例如调用认证服务
        return token != null;
    }
}