package com.oneself.config;

import com.oneself.filter.TraceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2025/1/21
 * packageName com.oneself.config
 * className TraceConfig
 * description
 * version 1.0
 */
@Configuration(proxyBeanMethods = false)
public class TraceConfig {
    @Bean
    public TraceFilter traceFilter() {
        return new TraceFilter();
    }
}
