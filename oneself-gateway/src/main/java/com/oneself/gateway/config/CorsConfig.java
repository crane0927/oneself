package com.oneself.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/12/24
 * packageName com.oneself.config
 * className CorsConfig
 * description Gateway CORS 跨域配置
 * version 1.0
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许所有源（生产环境建议指定具体域名）
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));
        
        // 允许发送凭证（Cookie、Authorization 等）
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return new CorsWebFilter(source);
    }
}

