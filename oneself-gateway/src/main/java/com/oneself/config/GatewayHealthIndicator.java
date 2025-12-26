package com.oneself.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.config
 * className GatewayHealthIndicator
 * description Gateway 健康检查指示器
 * version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayHealthIndicator implements HealthIndicator {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Health health() {
        try {
            // 检查 Redis 连接
            String ping = null;
            if (redisTemplate.getConnectionFactory() != null) {
                ping = redisTemplate.getConnectionFactory()
                        .getConnection()
                        .ping();
            }

            if ("PONG".equals(ping)) {
                return Health.up()
                        .withDetail("redis", "连接正常")
                        .withDetail("status", "UP")
                        .build();
            } else {
                return Health.down()
                        .withDetail("redis", "连接异常")
                        .withDetail("status", "DOWN")
                        .build();
            }
        } catch (Exception e) {
            log.error("健康检查失败", e);
            return Health.down()
                    .withDetail("redis", "连接失败: " + e.getMessage())
                    .withDetail("status", "DOWN")
                    .build();
        }
    }
}

