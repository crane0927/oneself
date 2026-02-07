package com.oneself.common.feature.web.handler;

import com.oneself.common.core.exception.OneselfException;
import com.oneself.common.core.exception.OneselfLoginException;
import com.oneself.common.feature.web.filter.TraceFilter;
import com.oneself.common.core.resp.Resp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author liuhuan
 * date 2025/1/22
 * packageName com.oneself.gateway.handler
 * className GlobalExceptionHandler
 * description 全局异常处理程序
 * version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 在类的开头定义常量
    private static final String LOG_MESSAGE_TEMPLATE = "请求：{}，发生异常：{}";

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(OneselfException.class)
    public Resp<Object> handleOneselfException(OneselfException e, HttpServletRequest request) {
        log.info(LOG_MESSAGE_TEMPLATE, request.getRequestURL(), e.getMessage(), e);
        return Resp.failure(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(), MDC.get(TraceFilter.TRACE_ID));
    }

    /**
     * 处理登录异常
     *
     * @param e 业务异常
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(OneselfLoginException.class)
    public Resp<Object> handleOneselfLoginException(OneselfLoginException e, HttpServletRequest request) {
        log.info(LOG_MESSAGE_TEMPLATE, request.getRequestURL(), e.getMessage(), e);
        return Resp.failure(e.getMessage(), HttpStatus.UNAUTHORIZED,
                request.getRequestURI(), MDC.get(TraceFilter.TRACE_ID));
    }


    /**
     * 处理绑定异常
     *
     * @param e 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(BindException.class)
    public Resp<Object> handleBindException(BindException e, HttpServletRequest request) {
        log.info(LOG_MESSAGE_TEMPLATE, request.getRequestURL(), e.getMessage(), e);
        return Resp.failure(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(), MDC.get(TraceFilter.TRACE_ID));
    }

    /**
     * 处理其他异常
     *
     * @param e 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    public Resp<Object> handleException(Exception e, HttpServletRequest request) {
        log.info(LOG_MESSAGE_TEMPLATE, request.getRequestURL(), e.getMessage(), e);
        return Resp.failure("系统异常，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(), MDC.get(TraceFilter.TRACE_ID));
    }
}

