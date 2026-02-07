package com.oneself.common.feature.web.config;

import com.oneself.common.feature.web.filter.TraceFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2025/1/21
 * packageName com.oneself.config
 * className TraceConfig
 * description 跟踪（仅 Servlet 环境生效，Gateway 等 WebFlux 应用不加载）
 * version 1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
public class TraceConfig {
    @Bean
    public TraceFilter traceFilter() {
        return new TraceFilter();
    }
}

