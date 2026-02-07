package com.oneself.common.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2025/9/30
 * packageName com.oneself.config
 * className JacksonConfig
 * description
 * version 1.0
 */
@Slf4j
@Configuration
public class JacksonConfig {

    /**
     * 创建并配置 ObjectMapper Bean
     * 配置：支持 java.time 时间类型、禁用时间戳格式、忽略未知字段等
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 注册JavaTimeModule，解决LocalDateTime/LocalDate等类型序列化问题
        objectMapper.registerModule(new JavaTimeModule());

        // 禁用时间戳格式（默认会把时间转为毫秒数，禁用后输出ISO8601格式）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 忽略未知字段（避免JSON中有多余字段时反序列化失败）
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 允许空字符串转换为枚举（解决之前的枚举转换问题）
        objectMapper.coercionConfigFor(Enum.class)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

        return objectMapper;
    }
}
