package com.oneself.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.utils
 * className JwtUtils
 * description
 * version 1.0
 */
@Slf4j
public class JwtUtils {
    /**
     * 默认有效期：1小时（60*60*1000毫秒）
     */
    public static final Long JWT_DEFAULT_TTL = 60 * 60 * 1000L;

    /**
     * 安全密钥（生产环境需替换为 ≥32个 字符的随机字符串，建议从 application.yml 注入）
     * 示例：通过 openssl rand -hex 32 生成安全密钥
     */
    public static final String JWT_SECRET = "oneself-token-32-chars-safe-key-12345678";

    /**
     * 签发者（可根据项目修改，如公司/系统名称）
     */
    public static final String JWT_ISSUER = "oneself";


    /**
     * 生成无符号UUID（移除"-"）
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 生成JWT（使用默认有效期 1 小时，自动生成唯一ID）
     *
     * @param subject 令牌主题（建议存放 JSON 格式数据，如 {"userId":123,"username":"admin"}）
     * @return 加密后的 JWT 字符串
     */
    public static String createJWT(String subject) {
        return createJWT(subject, JWT_DEFAULT_TTL);
    }


    /**
     * 生成 JWT（自定义有效期，自动生成唯一 ID）
     *
     * @param subject   令牌主题（JSON 格式数据）
     * @param ttlMillis 自定义有效期（毫秒），null 则使用默认 1 小时
     * @return 加密后的 JWT 字符串
     */
    public static String createJWT(String subject, Long ttlMillis) {
        return createJWT(getUUID(), subject, ttlMillis);
    }


    /**
     * 生成 JWT（完全自定义：ID、主题、有效期）
     *
     * @param id        令牌唯一ID（建议使用 UUID，避免重复）
     * @param subject   令牌主题（JSON 格式数据）
     * @param ttlMillis 有效期（毫秒），null 则使用默认 1 小时
     * @return 加密后的 JWT 字符串
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        // 1. 校验核心参数（避免空值导致异常）
        if (Objects.isNull(id) || id.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT 唯一 ID 不能为空");
        }
        if (Objects.isNull(subject) || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT 主题（subject）不能为空");
        }

        // 2. 获取JWT构建器（封装通用逻辑）
        JwtBuilder builder = getJwtBuilder(id, subject, ttlMillis);
        // 3. 压缩为JWT字符串并返回
        return builder.compact();
    }


    /**
     * 构建 JWT 核心配置（封装签发者、时间、签名等通用逻辑）
     *
     * @param id        令牌唯一ID
     * @param subject   令牌主题
     * @param ttlMillis 有效期（毫秒）
     * @return JwtBuilder 构建器
     */
    private static JwtBuilder getJwtBuilder(String id, String subject, Long ttlMillis) {
        // 3.1 生成安全签名密钥（HS256 算法，密钥 ≥256 位）
        SecretKey signingKey = generateSigningKey();

        // 3.2 处理有效期（nul l则使用默认 1 小时）
        long nowMillis = System.currentTimeMillis();
        Date issuedDate = new Date(nowMillis); // 签发时间
        long expMillis = nowMillis + (Objects.isNull(ttlMillis) ? JWT_DEFAULT_TTL : ttlMillis);
        Date expireDate = new Date(expMillis); // 过期时间

        // 3.3 构建 JWT
        return Jwts.builder()
                .id(id)                          // 唯一ID（jti字段）
                .subject(subject)                // 主题（sub字段，存放核心业务数据）
                .issuer(JWT_ISSUER)              // 签发者（iss字段）
                .issuedAt(issuedDate)            // 签发时间（iat字段）
                .expiration(expireDate)          // 过期时间（exp字段）
                .signWith(signingKey);           // 签名配置（替代setSigningKey，自动适配HS256算法）
    }


    /**
     * 生成安全签名密钥（适配JJWT 0.13.x，确保密钥≥256位）
     *
     * @return 符合HS256算法的SecretKey
     */
    public static SecretKey generateSigningKey() {
        // 校验密钥长度（HS256要求≥256位=32个字符，避免安全风险）
        if (Objects.isNull(JWT_SECRET) || JWT_SECRET.length() < 32) {
            throw new IllegalArgumentException("JWT 密钥不能为空且长度不能小于 32 个字符（256 位）");
        }

        // 将字符串密钥转换为SecretKey（使用UTF-8编码，避免平台默认编码差异）
        byte[] secretBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(secretBytes);
    }


    /**
     * 解析JWT（校验签名、过期时间，返回负载信息）
     *
     * @param jwt 待解析的 JWT 字符串
     * @return Claims JWT 负载（包含 id、subject、iss、iat、exp 等字段）
     * @throws Exception 解析失败（签名无效、过期、格式错误等）
     */
    public static Claims parseJWT(String jwt) throws Exception {
        // 1. 校验JWT非空
        if (Objects.isNull(jwt) || jwt.trim().isEmpty()) {
            throw new IllegalArgumentException("待解析的 JWT 字符串不能为空");
        }

        try {
            // 2. 获取签名密钥（与生成JWT时使用同一密钥）
            SecretKey signingKey = generateSigningKey();

            // 3. 解析JWT
            return Jwts.parser()
                    .verifyWith(signingKey)          // 验证签名（替代setSigningKey）
                    .build()                         // 构建解析器
                    .parseSignedClaims(jwt)          // 解析带签名的JWT
                    .getPayload();                   // 获取负载信息

        } catch (SignatureException e) {
            // 签名无效（密钥不匹配、JWT被篡改）
            log.error("JWT  签名验证失败：JWT= {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("令牌签名无效，可能已被篡改，请重新登录", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 令牌过期
            log.error("JWT 已过期：JWT = {}, 过期时间 = {}", jwt, e.getClaims().getExpiration(), e);
            throw new Exception("令牌已过期，请重新登录", e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // 令牌格式错误（非标准JWT、字段缺失等）
            log.error("JWT 格式错误：JWT = {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("令牌格式错误，请检查请求合法性", e);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            // 不支持的JWT类型（如算法不匹配）
            log.error("不支持的 JWT 类型：JWT = {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("不支持当前令牌类型，请使用标准HS256算法令牌", e);
        } catch (Exception e) {
            // 其他异常（如密钥长度不足、负载解析失败）
            log.error("JWT 解析失败：JWT = {}, 原因 = {}", jwt, e.getMessage(), e);
            throw new Exception("令牌解析异常，请联系管理员", e);
        }
    }


    /**
     * 测试方法：生成JWT并解析
     */
    public static void main(String[] args) throws Exception {
        // 1. 准备主题数据（建议JSON格式，存放用户ID、角色等核心信息）
//        String subject = "{\"userId\":123,\"username\":\"admin\",\"role\":\"ADMIN\"}";
//
//        // 2. 生成JWT（自定义有效期2小时）
//        String jwt = createJWT(subject, 2 * 60 * 60 * 1000L);
//        log.info("生成的JWT：\n{}", jwt);

        // 3. 解析JWT
        Claims claims = parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0OWVjZTgxNDBlZjI0MWJkOGM2MmU5ZWRlZTdlMjI5ZSIsInN1YiI6IntcIkBjbGFzc1wiOlwiY29tLm9uZXNlbGYubW9kZWwuYm8uTG9naW5Vc2VyU2Vzc2lvbkJPXCIsXCJ1c2VySWRcIjpcImM4YjNjNTcwMjU4MGQwYzNmYjIyZTIxYjE5ZDk4ZWRlXCIsXCJ1c2VybmFtZVwiOlwiY3JhbmVcIixcImlwXCI6XCIxMjcuMC4wLjFcIixcImRldmljZVwiOlwiT3RoZXJcIixcImJyb3dzZXJcIjpcIk90aGVyXCIsXCJzZXNzaW9uSWRcIjpcIjIyOTViZjFlXCIsXCJsb2dpblRpbWVcIjpcIlRodSBTZXAgMTEgMjE6MDU6NTkgQ1NUIDIwMjVcIn0iLCJpc3MiOiJvbmVzZWxmIiwiaWF0IjoxNzU3NTk1OTU5LCJleHAiOjE3NTc1OTk1NTl9.bUzBTklNRAdruJd2D43SLFD9JNYQ3vfQv2Wyfx0jcWg");
        log.info("\n解析JWT结果：");
        log.info("唯一ID（jti）：{}", claims.getId());
        log.info("主题（sub）：{}", claims.getSubject());
        log.info("签发者（iss）：{}", claims.getIssuer());
        log.info("签发时间（iat）：{}", new Date(claims.getIssuedAt().getTime()));
        log.info("过期时间（exp）：{}", new Date(claims.getExpiration().getTime()));

        JsonNode jsonNode = JacksonUtils.fromJsonToNode(claims.getSubject());
        String userId = jsonNode.get("userId").asText();
        log.info("从主题中提取的用户 ID：{}", userId);
    }
}

