package com.oneself.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.config
 * className CorsConfig
 * description CORS 跨域配置
 * version 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名（使用 allowedOriginPatterns 支持通配符）
                .allowedOriginPatterns("*")
                // 是否允许 cookie
                .allowCredentials(true)
                // 设置允许的请求方式（包含 OPTIONS 用于 CORS 预检请求）
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS", "PATCH")
                // 设置允许的 header 属性
                .allowedHeaders("*")
                // 跨域允许时间（预检请求的缓存时间，单位：秒）
                .maxAge(3600);
    }
}

