package com.oneself;

import com.oneself.aspect.RespEnhanceAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.validation.annotation.Validated;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself
 * className GatewayApplication
 * description Gateway 启动类
 * version 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@Validated
@ComponentScan(
        basePackages = "com.oneself",
        excludeFilters = {
                // 排除使用 Servlet API 的配置类
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {com.oneself.config.TraceConfig.class}
                ),
                // 排除使用 Servlet API 的 AOP 切面
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {RespEnhanceAspect.class}
                ),
                // 排除使用 Servlet API 的异常处理器
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {com.oneself.handler.GlobalExceptionHandler.class}
                ),
                // 排除使用 Servlet API 的 SecurityUtils（Gateway 使用响应式方式处理认证）
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {com.oneself.utils.SecurityUtils.class}
                ),
                // 排除使用 Servlet API 的 RequireAuthAspect（Gateway 在过滤器层处理认证）
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {com.oneself.aspect.RequireAuthAspect.class}
                ),
                // 排除依赖 SecurityUtils 的 SecurityCurrentUserProvider
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {com.oneself.provider.SecurityCurrentUserProvider.class}
                )
        }
)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
