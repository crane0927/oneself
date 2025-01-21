package com.oneself.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * @author liuhuan
 * date 2025/1/21
 * packageName com.oneself.filter
 * className TraceFilter
 * description 生成一个唯一的Trace ID 并存储在 Log4j2 ThreadContext 中用于分布式跟踪
 * version 1.0
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(urlPatterns = "/**", filterName = "TraceFilter")
public class TraceFilter extends OncePerRequestFilter {
    public static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 生成唯一 traceId 并放入 ThreadContext
        ThreadContext.put(TRACE_ID, UUID.randomUUID().toString());

        try {
            // 记录请求链路日志
            filterChain.doFilter(request, response);
        } finally {
            // 清理 ThreadContext
            ThreadContext.clearAll();
        }
    }
}