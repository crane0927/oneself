package com.oneself.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author liuhuan
 * date 2025/1/20
 * packageName com.oneself.config
 * className ValidatorConfig
 * description 数据验证配置
 * version 1.0
 */
@Configuration
public class ValidatorConfig {
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}