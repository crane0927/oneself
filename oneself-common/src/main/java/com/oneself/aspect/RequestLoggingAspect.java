package com.oneself.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.SensitiveDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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
        // 记录请求参数
        logRequestParameters(joinPoint);
        // 执行目标方法
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Error occurred: {}", throwable.getMessage(), throwable);
            throw throwable;
        }
        // 记录响应信息
        logResponse(result);
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