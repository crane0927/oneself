package com.oneself.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.SensitiveDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.aspect
 * className RequestLoggingAspect
 * description 记录日志的切面
 * version 1.0
 */
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    private final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();

    @Around("@within(com.oneself.annotation.RequestLogging) || @annotation(com.oneself.annotation.RequestLogging)")
    public Object logDetails(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取方法级别的 @RequestLogging 注解，优先使用方法级别注解
        boolean logRequest = true;
        boolean logResponse = true;

        // 获取目标方法
        Method method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
        // 如果方法级别有 @Loggable 注解，优先使用方法级别的设置
        if (method.isAnnotationPresent(com.oneself.annotation.RequestLogging.class)) {
            com.oneself.annotation.RequestLogging requestLogging = method.getAnnotation(com.oneself.annotation.RequestLogging.class);
            logRequest = requestLogging.logRequest();
            logResponse = requestLogging.logResponse();
        } else if (joinPoint.getTarget().getClass().isAnnotationPresent(com.oneself.annotation.RequestLogging.class)) {
            // 如果方法级别没有注解，再检查类级别的 @Loggable 注解
            com.oneself.annotation.RequestLogging requestLogging = joinPoint.getTarget().getClass().getAnnotation(com.oneself.annotation.RequestLogging.class);
            logRequest = requestLogging.logRequest();
            logResponse = requestLogging.logResponse();
        }

        // 记录请求参数（如果 logRequest 为 true）
        if (logRequest) {
            logRequestParameters(joinPoint);
        }

        // 执行目标方法
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Error occurred: {}", throwable.getMessage(), throwable);
            throw throwable;
        }

        // 记录响应结果（如果 logResponse 为 true）
        if (logResponse) {
            logResponse(result);
        }
        return result;
    }

    /**
     * 记录请求参数
     */
    private void logRequestParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            try {
                String param = objectMapper.writeValueAsString(SensitiveDataUtils.copyAndMaskSensitiveData(args[i]));
                log.info("Request Parameter [{}]: {}", i + 1, param);
            } catch (Exception e) {
                log.warn("Failed to serialize parameter [{}]: {}", i + 1, e.getMessage());
            }
        }
    }

    /**
     * 记录响应结果
     */
    private void logResponse(Object result) {
        try {
            Object maskedResult = SensitiveDataUtils.copyAndMaskSensitiveData(result);
            String response = objectMapper.writeValueAsString(maskedResult);
            log.info("Response: {}", response);
        } catch (Exception e) {
            log.warn("Failed to serialize response: {}", e.getMessage());
        }
    }
}