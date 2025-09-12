package com.oneself.service.impl;

import com.oneself.config.RsaKeyConfig;
import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserBO;
import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.model.dto.LoginDTO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.service.AuthService;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.service.impl
 * className AuthServiceImpl
 * description
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final RsaKeyConfig rsaKeyConfig;

    @Override
    public String login(LoginDTO dto, HttpServletRequest request) {
        // 1. RSA 私钥解密
        String password;
        try {
            password = rsaKeyConfig.decrypt(dto.getPassword());
        } catch (Exception e) {
            log.error("用户 {} 密码解密失败", dto.getUsername(), e);
            throw new RuntimeException("密码解密失败");
        }

        // 2. Spring Security 验证用户名密码
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }

        LoginUserBO bo = (LoginUserBO) authenticate.getPrincipal();
        String userId = bo.getId();
        String username = bo.getUsername();

        // 3. 收集客户端信息
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String device = parseDevice(userAgent);
        String browser = parseBrowser(userAgent);

        // 4. 生成 sessionId
        String sessionId = JwtUtils.getUUID().substring(0, 8);

        // 5. 构建 JWT subject
        LoginUserSessionBO loginUserSessionBO = LoginUserSessionBO.builder()
                .userId(userId)
                .username(username)
                .ip(ip)
                .device(device)
                .browser(browser)
                .sessionId(sessionId)
                .loginTime(System.currentTimeMillis())
                .build();
        String subjectJson = JacksonUtils.toJsonString(loginUserSessionBO);

        // 6. 生成 JWT token
        String token = JwtUtils.createJWT(subjectJson);

        // 7. 存 Redis 会话 (String + TTL)
        String redisKey = buildLoginKey(sessionId);
        long ttlSeconds = 3600; // 1小时
        redisTemplate.opsForValue().set(redisKey, JacksonUtils.toJsonString(bo), ttlSeconds, TimeUnit.SECONDS);

        // 8. 用户维度存储 sessionId（SortedSet + expireAt）
        String userKey = buildUserSessionsKey(userId);
        long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttlSeconds);
        redisTemplate.opsForZSet().add(userKey, sessionId, expireAt);

        // 9. 可选：清理过期 sessionId
        redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

        log.info("用户 {} 登录成功，sessionId={}", username, sessionId);
        return token;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        // 获取 token
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            throw new OneselfException("未找到 token");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 解析 JWT
            var claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            LoginUserSessionBO bo = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

            String sessionId = bo.getSessionId();
            String userId = bo.getUserId();

            // 删除 Redis 会话
            redisTemplate.delete(buildLoginKey(sessionId));

            // 删除 SortedSet 中的 sessionId
            String userKey = buildUserSessionsKey(userId);
            redisTemplate.opsForZSet().remove(userKey, sessionId);

            // 清理 SecurityContext
            SecurityContextHolder.clearContext();

            log.info("用户 {} 登出成功，sessionId={}", bo.getUsername(), sessionId);
            return true;
        } catch (Exception e) {
            log.error("登出失败", e);
            throw new OneselfException("登出失败，请重试");
        }
    }

    // ------------------------ 工具方法 ------------------------

    private String buildLoginKey(String sessionId) {
        return RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
    }

    private String buildUserSessionsKey(String userId) {
        return RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 解析设备类型
     *
     * @param ua User-Agent 字符串
     * @return 设备类型（Windows、Mac、iPhone、Android、Other）
     */
    private String parseDevice(String ua) {
        if (ua == null) return "Unknown";
        if (ua.contains("Windows")) return "Windows";
        if (ua.contains("Mac")) return "Mac";
        if (ua.contains("iPhone")) return "iPhone";
        if (ua.contains("Android")) return "Android";
        return "Other";
    }

    /**
     * 解析浏览器类型
     *
     * @param ua User-Agent 字符串
     * @return 浏览器类型（Chrome、Firefox、Safari、Edge、Other）
     */
    private String parseBrowser(String ua) {
        if (ua == null) return "Unknown";
        if (ua.contains("Chrome")) return "Chrome";
        if (ua.contains("Firefox")) return "Firefox";
        if (ua.contains("Safari") && !ua.contains("Chrome")) return "Safari";
        if (ua.contains("Edge")) return "Edge";
        return "Other";
    }
}
