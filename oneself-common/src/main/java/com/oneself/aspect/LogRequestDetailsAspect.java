package com.oneself.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.filter.TraceFilter;
import com.oneself.model.vo.ResponseVO;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.SensitiveDataUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
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

        // 获取请求信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        String url = httpServletRequest.getRequestURL().toString();
        String uri = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();

        log.info("=== Request Details ===");
        log.info("Request URL[{}]: {} ", method, url);
        log.info("Class Method: {}.{}", className, methodName);

        // 记录请求参数
        logRequestParameters(joinPoint);

        // 执行目标方法
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Error occurred: {}", throwable.getMessage(), throwable);
            throw throwable;
        } finally {
            // 如果返回值是 ResponseVO，设置 path 和 traceId
            if (result instanceof ResponseVO) {
                ((ResponseVO<?>) result).setPath(uri);
                ((ResponseVO<?>) result).setTraceId(ThreadContext.get(TraceFilter.TRACE_ID));
            }
        }


        // 记录响应信息
        logResponse(result, System.currentTimeMillis() - startTime);
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
    private void logResponse(Object result, long duration) {
        try {
            Object maskedResult = SensitiveDataUtils.copyAndMaskSensitiveData(result);
            String response = objectMapper.writeValueAsString(maskedResult);
            log.info("Response: {}", response);
        } catch (Exception e) {
            log.warn("Failed to serialize response: {}", e.getMessage());
        }
        log.info("Total Time: {}ms", duration);
        log.info("==== Request End ====");
    }
}