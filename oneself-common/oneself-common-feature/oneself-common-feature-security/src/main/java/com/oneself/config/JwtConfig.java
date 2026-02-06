package com.oneself.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.config
 * className JwtConfig
 * description JWT 配置类，从环境变量读取密钥
 * version 1.0
 */
@Slf4j
@Getter
@Configuration
public class JwtConfig {

    /** dev 环境下未配置或长度不足时使用的默认密钥（仅用于本地开发，生产必须配置 ONESELF_JWT_SECRET） */
    private static final String DEV_DEFAULT_SECRET = "oneself-jwt-dev-default-secret-32chars!!";

    private final String secret;
    private final String issuer;

    /**
     * JWT 配置构造函数
     *
     * @param secret JWT 密钥（从环境变量 ONESELF_JWT_SECRET 读取，至少32个字符）
     * @param issuer JWT 签发者（从环境变量 ONESELF_JWT_ISSUER 读取，默认为 "oneself"）
     * @param activeProfiles 当前激活的 profile，逗号分隔（用于 dev 下允许默认密钥）
     */
    public JwtConfig(
            @Value("${ONESELF_JWT_SECRET:}") String secret,
            @Value("${ONESELF_JWT_ISSUER:oneself}") String issuer,
            @Value("${spring.profiles.active:}") String activeProfiles) {

        boolean isDev = activeProfiles != null && activeProfiles.contains("dev");
        String resolvedSecret = (secret != null && secret.length() >= 32) ? secret : null;

        if (resolvedSecret == null && isDev) {
            resolvedSecret = DEV_DEFAULT_SECRET;
            log.warn("ONESELF_JWT_SECRET 未配置或长度不足，dev 环境使用默认密钥。生产环境请设置：export ONESELF_JWT_SECRET=$(openssl rand -hex 32)");
        }

        if (resolvedSecret == null) {
            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalStateException(
                        "ONESELF_JWT_SECRET 环境变量未配置。请设置环境变量，例如：\n" +
                        "export ONESELF_JWT_SECRET=$(openssl rand -hex 32)");
            }
            throw new IllegalStateException(
                    String.format("ONESELF_JWT_SECRET 长度不足，当前长度：%d，至少需要32个字符（256位）", secret.length()));
        }

        this.secret = resolvedSecret;
        this.issuer = issuer;

        log.info("JWT 配置初始化成功，issuer={}, secretLength={}", issuer, this.secret.length());
    }
}

