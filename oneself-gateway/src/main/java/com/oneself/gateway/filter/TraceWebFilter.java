package com.oneself.gateway.filter;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.filter
 * className TraceWebFilter
 * description Gateway 响应式 Trace Filter，生成唯一的 Trace ID 并存储在 Logback MDC 中用于分布式跟踪
 * version 1.0
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TraceWebFilter implements WebFilter {
    
    public static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();

        // 获取或生成 traceId
        String traceId = getOrGenerateTraceId(exchange);
        
        // 设置响应头
        exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, traceId);

        // 记录请求详情
        logRequestDetails(exchange, traceId);

        // 在响应式上下文中设置 MDC
        return chain.filter(exchange)
                .contextWrite(context -> {
                    // 将 traceId 放入响应式上下文
                    MDC.put(TRACE_ID, traceId);
                    return context;
                })
                .doFinally(signalType -> {
                    // 记录处理时间
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Request completed in {} ms", duration);
                    log.info("===== Request End =====");

                    // 清理 MDC
                    MDC.clear();
                });
    }

    /**
     * 获取或生成Trace ID
     *
     * @param exchange ServerWebExchange
     * @return Trace ID
     */
    private String getOrGenerateTraceId(ServerWebExchange exchange) {
        // 尝试从请求头获取Trace ID（支持分布式追踪）
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (StringUtils.isNotBlank(traceId)) {
            return traceId;
        }

        // 生成新的Trace ID
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 记录请求详情
     *
     * @param exchange ServerWebExchange
     * @param traceId Trace ID
     */
    private void logRequestDetails(ServerWebExchange exchange, String traceId) {
        log.info("=== Request Details ===");
        log.info("Trace ID: {}", traceId);
        log.info("Request Method: {}", exchange.getRequest().getMethod());
        log.info("Request URL: {}", exchange.getRequest().getURI());
        log.info("Query String: {}", exchange.getRequest().getQueryParams());
        log.info("Client IP: {}", getClientIpAddress(exchange));
        log.info("User Agent: {}", exchange.getRequest().getHeaders().getFirst("User-Agent"));
    }

    /**
     * 获取客户端 IP 地址
     *
     * @param exchange ServerWebExchange
     * @return 客户端 IP
     */
    private String getClientIpAddress(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp)) {
            return xRealIp;
        }

        return exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
                : "unknown";
    }
}

