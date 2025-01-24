package com.oneself.handler;

import com.oneself.exception.OneselfException;
import com.oneself.exception.OneselfLoginException;
import com.oneself.filter.TraceFilter;
import com.oneself.model.vo.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author liuhuan
 * date 2025/1/22
 * packageName com.oneself.handler
 * className GlobalExceptionHandler
 * description 全局异常处理程序
 * version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(OneselfException.class)
    public ResponseVO<Object> handleOneselfException(OneselfException e, HttpServletRequest request) {
        log.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        return ResponseVO.failure(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(), ThreadContext.get(TraceFilter.TRACE_ID));
    }

    /**
     * 处理登录异常
     *
     * @param e 业务异常
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(OneselfLoginException.class)
    public ResponseVO<Object> handleOneselfLoginException(OneselfLoginException e, HttpServletRequest request) {
        log.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        return ResponseVO.failure(e.getMessage(), HttpStatus.UNAUTHORIZED,
                request.getRequestURI(), ThreadContext.get(TraceFilter.TRACE_ID));
    }


    /**
     * 处理绑定异常
     *
     * @param e 异常对象
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(BindException.class)
    public ResponseVO<Object> handleBindException(BindException e, HttpServletRequest request) {
        log.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        return ResponseVO.failure(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(), ThreadContext.get(TraceFilter.TRACE_ID));
    }

    /**
     * 处理其他异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO<Object> handleException(Exception e, HttpServletRequest request) {
        log.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        return ResponseVO.failure("系统异常，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI(), ThreadContext.get(TraceFilter.TRACE_ID));
    }
}
