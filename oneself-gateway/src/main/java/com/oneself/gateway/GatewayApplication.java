package com.oneself.gateway;

import com.oneself.common.feature.web.aspect.RespEnhanceAspect;
import com.oneself.common.feature.security.aspect.RequireAuthAspect;
import com.oneself.common.feature.security.provider.SecurityCurrentUserProvider;
import com.oneself.common.feature.security.utils.SecurityUtils;
import com.oneself.common.feature.web.config.TraceConfig;
import com.oneself.common.feature.web.handler.GlobalExceptionHandler;
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
                        classes = {TraceConfig.class}
                ),
                // 排除使用 Servlet API 的 AOP 切面
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {RespEnhanceAspect.class}
                ),
                // 排除使用 Servlet API 的异常处理器
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {GlobalExceptionHandler.class}
                ),
                // 排除使用 Servlet API 的 SecurityUtils（Gateway 使用响应式方式处理认证）
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityUtils.class}
                ),
                // 排除使用 Servlet API 的 RequireAuthAspect（Gateway 在过滤器层处理认证）
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {RequireAuthAspect.class}
                ),
                // 排除依赖 SecurityUtils 的 SecurityCurrentUserProvider
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityCurrentUserProvider.class}
                )
        }
)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
