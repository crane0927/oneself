package com.oneself.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.config
 * className MvcConfig
 * description Spring MVC 配置
 * version 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 对所有的接口路径启用跨域配置（/** 表示匹配所有路径）
//                .allowedOrigins("*") // 允许所有的域名发起跨域请求（不推荐用于生产环境，特别是与 allowCredentials(true) 同时使用）
//                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 请求方法
//                .allowCredentials(true) // 允许发送 Cookie 和其他凭证信息（注意：此时不能使用 allowedOrigins("*")，应指定具体域名）
//                .maxAge(3600) // 预检请求的缓存时间（单位：秒），表示在3600秒内浏览器不会重复发送预检请求
//                .allowedHeaders("*"); // 允许所有的请求头（例如：Content-Type、Authorization 等）
    }
}
