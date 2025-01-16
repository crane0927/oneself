package com.oneself.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.SensitiveDataUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.aspect
 * className LogRequestDetailsAspect
 * description 记录日志的切面
 * version 1.0
 */
@Slf4j
@Aspect
@Component
public class LogRequestDetailsAspect {

    private final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();
    private final HttpServletRequest httpServletRequest;

    public LogRequestDetailsAspect(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Around("@within(com.oneself.annotation.LogRequestDetails) || @annotation(com.oneself.annotation.LogRequestDetails)")
    public Object logDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取类全路径和方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

        // 获取请求相关信息
        String url = httpServletRequest.getRequestURL().toString();
        String method = httpServletRequest.getMethod();

        // 深拷贝并屏蔽请求参数中的敏感数据
        Object[] args = joinPoint.getArgs();
        Object[] maskedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            maskedArgs[i] = SensitiveDataUtils.copyAndMaskSensitiveData(args[i]);
        }
        String params = objectMapper.writeValueAsString(maskedArgs);

        log.info("=== Request Details ===");
        log.info("Request URL: {} [{}]", url, method);
        log.info("Class: {} Method: {}", className, methodName);
        log.info("Request Parameters: {}", params);

        // 执行目标方法
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Error Occurred: {}", throwable.getMessage());
            throw throwable;
        }

        // 深拷贝并屏蔽返回值中的敏感数据
        Object maskedResult = SensitiveDataUtils.copyAndMaskSensitiveData(result);
        String response = objectMapper.writeValueAsString(maskedResult);

        // 获取总耗时
        long totalTime = System.currentTimeMillis() - startTime;


        log.info("Response: {}", response);
        log.info("Total Time: {}ms", totalTime);
        log.info("=======================");

        return result;
    }
}
