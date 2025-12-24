package com.oneself.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
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
 * description 生成一个唯一的Trace ID 并存储在 Logback MDC 中用于分布式跟踪
 * version 1.0
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(urlPatterns = "/**", filterName = "TraceFilter")
public class TraceFilter extends OncePerRequestFilter {
    public static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            // 获取或生成 traceId
            String traceId = getOrGenerateTraceId(request);
            MDC.put(TRACE_ID, traceId);

            // 设置响应头
            response.setHeader(TRACE_ID_HEADER, traceId);

            // 记录请求详情
            logRequestDetails(request);

            // 继续执行过滤器链
            filterChain.doFilter(request, response);

        } finally {
            // 记录处理时间
            long duration = System.currentTimeMillis() - startTime;
            log.info("Request completed in {} ms", duration);
            log.info("===== Request End =====");

            // 清理 MDC
            MDC.clear();
        }
    }

    /**
     * 获取或生成Trace ID
     *
     * @param request HTTP请求
     * @return Trace ID
     */
    private String getOrGenerateTraceId(HttpServletRequest request) {
        // 尝试从请求头获取Trace ID（支持分布式追踪）
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (StringUtils.isNotBlank(traceId)) {
            return traceId;
        }

        // 生成新的Trace ID
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 记录请求详情
     *
     * @param request HTTP请求
     */
    private void logRequestDetails(HttpServletRequest request) {
        log.info("=== Request Details ===");
        log.info("Trace ID: {}", MDC.get(TRACE_ID));
        log.info("Request Method: {}", request.getMethod());
        log.info("Request URL: {}", request.getRequestURL().toString());
        log.info("Query String: {}", request.getQueryString());
        log.info("Client IP: {}", getClientIpAddress(request));
        log.info("User Agent: {}", request.getHeader("User-Agent"));
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return 客户端IP
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
