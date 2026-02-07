package com.oneself.gateway.handler;

import com.oneself.common.core.resp.Resp;
import com.oneself.common.core.utils.JacksonUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.gateway.handler
 * className GatewayExceptionHandler
 * description Gateway 全局异常处理器（响应式）
 * version 1.0
 */
@Slf4j
@Component
@Order(-2) // 优先级高于默认的 ErrorWebExceptionHandler
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 根据异常类型设置状态码和错误信息
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "系统异常，请稍后重试";
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.resolve(rse.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            message = rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = "请求超时，请稍后重试";
        } else {
            log.error("Gateway 异常处理，path={}, error={}", 
                    exchange.getRequest().getPath(), ex.getMessage(), ex);
        }
        
        response.setStatusCode(status);
        
        // 构建错误响应
        Resp<String> resp = Resp.failure(message, status);
        String json = JacksonUtils.toJsonString(resp);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        
        return response.writeWith(Mono.just(buffer));
    }
}

