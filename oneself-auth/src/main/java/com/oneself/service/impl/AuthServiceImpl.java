package com.oneself.service.impl;

import com.oneself.config.RsaKeyConfig;
import com.oneself.exception.OneselfException;
import com.oneself.model.bo.JwtSessionBO;
import com.oneself.model.bo.UserSessionBO;
import com.oneself.model.dto.LoginDTO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.service.AuthService;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.JwtUtils;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
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
import org.springframework.util.Assert;

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
    private final HttpServletRequest request;


    @Override
    public String login(LoginDTO dto, HttpServletRequest request) {
        Assert.notNull(dto, "请求参数不能为空");
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

        UserSessionBO bo = (UserSessionBO) authenticate.getPrincipal();
        String userId = bo.getId();
        String username = bo.getUsername();

        // 3. 收集客户端信息
        String ip = getClientIp(request);
        String device = parseDevice();
        String browser = parseBrowser();

        // 4. 生成 sessionId
        String sessionId = JwtUtils.getUUID().substring(0, 8);

        // 5. 构建 JWT subject
        JwtSessionBO jwtSessionBO = JwtSessionBO.builder()
                .userId(userId)
                .username(username)
                .ip(ip)
                .device(device)
                .browser(browser)
                .sessionId(sessionId)
                .loginTime(System.currentTimeMillis())
                .build();
        String subjectJson = JacksonUtils.toJsonString(jwtSessionBO);

        // 6. 生成 JWT token
        String token = JwtUtils.createJWT(subjectJson);

        // 7. 存 Redis 会话 (String + TTL)
        String redisKey = buildLoginKey(sessionId);
        long ttlSeconds = JwtUtils.JWT_DEFAULT_TTL; // 1 小时
        bo.setPassword(null);
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
            JwtSessionBO bo = JacksonUtils.fromJson(subject, JwtSessionBO.class);

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
     * 获取当前请求的 User-Agent 对象
     */
    private UserAgent getUserAgent() {
        String uaString = request.getHeader("User-Agent");
        return uaString != null ? UserAgent.parseUserAgentString(uaString) : null;
    }

    /**
     * 解析设备类型
     *
     * @return 设备类型（Desktop、Mobile、Tablet、Unknown）
     */
    public String parseDevice() {
        UserAgent userAgent = getUserAgent();
        if (userAgent == null) return "Unknown";

        OperatingSystem os = userAgent.getOperatingSystem();
        return os != null ? os.getDeviceType().getName() : "Unknown";
    }

    /**
     * 解析浏览器类型
     *
     * @return 浏览器类型（Chrome、Firefox、Safari、Edge、Other）
     */
    public String parseBrowser() {
        UserAgent userAgent = getUserAgent();
        if (userAgent == null) return "Unknown";

        Browser browser = userAgent.getBrowser();
        return browser != null ? browser.getName() : "Other";
    }
}
