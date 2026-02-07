package com.oneself.gateway.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.filter
 * className GatewayMetricsFilter
 * description Gateway 指标收集过滤器
 * version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayMetricsFilter implements GlobalFilter, Ordered {

    private final MeterRegistry meterRegistry;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getURI().getPath();
        exchange.getRequest().getMethod();
        String method = exchange.getRequest().getMethod().name();

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    int statusCode = status != null ? status.value() : 0;

                    // 记录请求指标
                    Timer.Sample sample = Timer.start(meterRegistry);
                    sample.stop(Timer.builder("gateway.request.duration")
                            .description("Gateway 请求处理时间")
                            .tag("method", method)
                            .tag("path", path)
                            .tag("status", String.valueOf(statusCode))
                            .register(meterRegistry));

                    // 记录请求计数
                    meterRegistry.counter("gateway.request.count",
                            "method", method,
                            "path", path,
                            "status", String.valueOf(statusCode))
                            .increment();

                    log.debug("记录指标，path={}, method={}, status={}, duration={}ms", 
                            path, method, statusCode, duration);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // 最后执行，确保能获取到响应状态码
    }
}

