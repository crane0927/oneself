package com.oneself.infra.mongodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.config
 * className MongoConfig
 * description
 * version 1.0
 */
@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider") // 开启审计
@EnableMongoRepositories(basePackages = {
        "com.oneself.script.repository.mongodb"
})
public class MongoConfig {

    /**
     * AuditorAware Bean
     * 用于返回当前操作用户
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * AuditorAware 实现类
     */
    static class AuditorAwareImpl implements AuditorAware<String> {
        @Override
        public Optional<String> getCurrentAuditor() {
            // TODO: 替换为实际获取当前登录用户名
            return Optional.of("admin");
        }
    }
}
