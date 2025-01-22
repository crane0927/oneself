package com.oneself.aspect;

import com.oneself.filter.TraceFilter;
import com.oneself.model.vo.ResponseVO;
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
 * date 2025/1/22
 * packageName com.oneself.aspect
 * className ResponseVOAspect
 * description 为返回结果设置 path 和 traceId
 * version 1.0
 */
@Slf4j
@Aspect
@Component
public class ResponseVOAspect {

    private final HttpServletRequest httpServletRequest;

    public ResponseVOAspect(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }


    @Around("execution(* com.oneself..controller..*(..))")
    public Object logDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        log.info("Class Method: {}.{}", className, methodName);
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
                ((ResponseVO<?>) result).setPath(httpServletRequest.getRequestURI());
                ((ResponseVO<?>) result).setTraceId(ThreadContext.get(TraceFilter.TRACE_ID));
            }
        }
        return result;
    }
}
