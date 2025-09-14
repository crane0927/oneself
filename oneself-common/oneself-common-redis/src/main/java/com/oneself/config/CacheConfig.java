package com.oneself.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.config
 * className CacheConfig
 * description
 * version 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 创建 ObjectMapper Bean 用于 Redis 序列化
     *
     * @return 配置好的 ObjectMapper 实例
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用时间戳格式（默认会把时间转为毫秒数，禁用后输出 "2025-09-12T10:00:00" 格式）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略未知字段（避免 JSON 中有多余字段时反序列化失败）
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 开启类型信息，但用 PROPERTY 方式
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );
        return objectMapper;
    }

    /**
     * 构建 RedisCacheManager Bean
     *
     * @param redisConnectionFactory Redis 连接工厂（Spring Boot 自动配置）
     * @param objectMapper           ObjectMapper 实例
     * @return RedisCacheManager 管理器
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                               ObjectMapper objectMapper) {
        // ========== 1. 全局默认配置 ==========
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(objectMapper)
                ))
                .computePrefixWith(cacheName -> RedisKeyPrefixEnum.SYSTEM_NAME.getPrefix() + cacheName + ":")
                .disableCachingNullValues();

        // ========== 2. 针对不同 cacheName 单独配置 ==========
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // 用户缓存：5~10 分钟随机 TTL
        cacheConfigs.put("sysUser",
                defaultConfig.entryTtl(Duration.ofMinutes(generateRandomTtl(5, 10))));

        // 部门缓存：30~40 分钟随机 TTL
        cacheConfigs.put("sysDept",
                defaultConfig.entryTtl(Duration.ofMinutes(generateRandomTtl(30, 40))));

        // 系统配置缓存：60~120 分钟随机 TTL
        cacheConfigs.put("sysConfiguration",
                defaultConfig.entryTtl(Duration.ofMinutes(generateRandomTtl(60, 120))));

        // ========== 3. 构建 RedisCacheManager ==========
        return RedisCacheManager.builder(redisConnectionFactory)
                // 设置全局默认配置
                .cacheDefaults(defaultConfig)
                // 初始化时应用不同 cacheName 的个性化配置
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    /**
     * 生成随机 TTL 值
     *
     * @param min 最小分钟数
     * @param max 最大分钟数
     * @return 随机 TTL 值
     */
    private int generateRandomTtl(int min, int max) {
        Random random = new Random();
        return min + random.nextInt(max - min + 1);
    }
}
