package com.oneself.aspect;

import com.oneself.filter.TraceFilter;
import com.oneself.model.vo.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
 * className ResponseVOEnhanceAspect
 * description 为 Controller 返回的 ResponseVO 自动注入请求路径 (path) 和追踪 ID (traceId)
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class ResponseVOEnhanceAspect {

    private final HttpServletRequest httpServletRequest;

    @Around("execution(* com.oneself..controller..*(..))")
    public Object enhanceResponseVO(ProceedingJoinPoint joinPoint) throws Throwable {
        String controllerClassName = joinPoint.getTarget().getClass().getName();
        String controllerMethodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        log.info("正在处理 Controller 请求：{}.{}", controllerClassName, controllerMethodName);

        // 执行目标Controller方法
        Object controllerResult = null;  // 变量名：controllerResult（明确是Controller的返回结果）
        try {
            controllerResult = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Controller 方法执行异常：{}.{}，异常信息：{}",
                    controllerClassName, controllerMethodName, throwable.getMessage(), throwable);
            throw throwable;  // 抛出异常，不影响原有异常处理流程
        } finally {
            // 仅当返回值是ResponseVO时，注入path和traceId
            if (controllerResult instanceof ResponseVO<?> responseVO) {
                responseVO.setPath(httpServletRequest.getRequestURI());
                responseVO.setTraceId(ThreadContext.get(TraceFilter.TRACE_ID));
                log.debug("已为 ResponseVO 注入 path：{}，traceId：{}",
                        responseVO.getPath(), responseVO.getTraceId());
            }
        }
        return controllerResult;
    }
}