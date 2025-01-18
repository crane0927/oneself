package com.oneself.aspect;

import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author liuhuan
 * date 2025/1/16
 * packageName com.oneself.aspect
 * className ApiResponseAspect
 * description 接口返回结果包装，添加请求路径
 * version 1.0
 */
@Aspect
@Component
public class ApiResponseAspect {

    @Around("execution(* com.oneself..controller..*(..))")
    public Object wrapApiResponse(ProceedingJoinPoint joinPoint) {
        Object result;
        String path;

        try {
            // 获取请求路径
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            path = request.getRequestURI();

            // 执行目标方法
            result = joinPoint.proceed();

            // 如果返回值是 ResponseVO，设置 path
            if (result instanceof ResponseVO) {
                ((ResponseVO<?>) result).setPath(path);
            }
            if (result instanceof PageVO<?>) {
                ((PageVO<?>) result).setPath(path);
            }
            // 返回结果
            return result;

        } catch (Throwable e) {
            // 捕获目标方法的异常并封装为统一的错误响应
            return ResponseVO.failure(e.getMessage());
        }
    }
}
