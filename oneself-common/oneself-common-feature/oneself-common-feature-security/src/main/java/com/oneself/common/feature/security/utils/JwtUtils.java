package com.oneself.common.feature.security.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.oneself.common.core.utils.JacksonUtils;
import com.oneself.common.feature.security.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.utils
 * className JwtUtils
 * description JWT 工具类，从配置类读取密钥
 * version 1.0
 */
@Slf4j
@Component
public class JwtUtils {

    private static JwtConfig jwtConfig;

    /**
     * 通过静态方法注入 JwtConfig（Spring 会在初始化后调用）
     */
    @Autowired
    public void setJwtConfig(JwtConfig jwtConfig) {
        JwtUtils.jwtConfig = jwtConfig;
        log.info("JwtUtils 已初始化，使用配置的 JWT 密钥");
    }

    /**
     * 获取 JWT 密钥
     *
     * @return JWT 密钥
     */
    private static String getJwtSecret() {
        if (jwtConfig == null) {
            throw new IllegalStateException("JwtConfig 未初始化，请确保 JwtUtils 已被 Spring 管理");
        }
        return jwtConfig.getSecret();
    }

    /**
     * 获取 JWT 签发者
     *
     * @return JWT 签发者
     */
    private static String getJwtIssuer() {
        if (jwtConfig == null) {
            return "oneself"; // 默认值
        }
        return jwtConfig.getIssuer();
    }

    /**
     * 生成无符号UUID（移除"-"）
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成 JWT（仅签名，不设置过期时间）
     *
     * @param subject 令牌主题（建议存放 JSON 格式数据，如 {"userId":123,"username":"admin"}）
     * @return 加密后的 JWT 字符串
     */
    public static String createJWT(String subject) {
        // 1. 校验参数
        if (Objects.isNull(subject) || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT 主题（subject）不能为空");
        }

        try {
            SecretKey signingKey = generateSigningKey();

            // 不设置 exp / iat，只签名 + subject
            return Jwts.builder()
                    .subject(subject)      // 主题（sub字段，存放业务数据）
                    .issuer(getJwtIssuer())    // 签发者
                    .signWith(signingKey)  // 签名配置
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("生成 JWT 失败", e);
        }
    }

    /**
     * 生成安全签名密钥（适配JJWT 0.13.x，确保密钥≥256位）
     *
     * @return 符合HS256算法的SecretKey
     */
    public static SecretKey generateSigningKey() {
        String secret = getJwtSecret();
        if (Objects.isNull(secret) || secret.length() < 32) {
            throw new IllegalArgumentException("JWT 密钥不能为空且长度不能小于 32 个字符（256 位）");
        }
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * 解析JWT（只校验签名，不做过期判断）
     *
     * @param jwt 待解析的 JWT 字符串
     * @return Claims JWT 负载（包含 sub、iss）
     * @throws Exception 解析失败（签名无效、格式错误等）
     */
    public static Claims parseJWT(String jwt) throws Exception {
        if (Objects.isNull(jwt) || jwt.trim().isEmpty()) {
            throw new IllegalArgumentException("待解析的 JWT 字符串不能为空");
        }

        try {
            SecretKey signingKey = generateSigningKey();

            return Jwts.parser()
                    .verifyWith(signingKey)          // 验证签名
                    .build()                         // 构建解析器
                    .parseSignedClaims(jwt)          // 解析带签名的JWT
                    .getPayload();                   // 获取负载（不含 exp）
        } catch (SignatureException e) {
            log.error("JWT 签名验证失败：JWT= {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("令牌签名无效，可能已被篡改，请重新登录", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("JWT 格式错误：JWT = {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("令牌格式错误，请检查请求合法性", e);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("不支持的 JWT 类型：JWT = {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("不支持当前令牌类型，请使用标准 HS256 算法令牌", e);
        } catch (Exception e) {
            log.error("JWT 解析失败：JWT = {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("令牌解析异常，请联系管理员", e);
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws Exception {
        String subject = "{\"userId\":\"123\",\"username\":\"admin\",\"sessionId\":\"abc123\"}";

        // 生成 JWT
        String jwt = createJWT(subject);
        log.info("生成的JWT：{}", jwt);

        // 解析 JWT
        Claims claims = parseJWT(jwt);
        log.info("\n解析JWT结果：");
        log.info("主题（sub）：{}", claims.getSubject());
        log.info("签发者（iss）：{}", claims.getIssuer());

        JsonNode jsonNode = JacksonUtils.fromJsonToNode(claims.getSubject());
        String userId = jsonNode.get("userId").asText();
        log.info("从主题中提取的用户 ID：{}", userId);
    }
}

