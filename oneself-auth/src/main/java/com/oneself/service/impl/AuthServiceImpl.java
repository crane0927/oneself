package com.oneself.service.impl;

import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserBO;
import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.model.dto.LoginDTO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.service.AuthService;
import com.oneself.utils.AssertUtils;
import com.oneself.utils.JacksonUtils;
import com.oneself.utils.JwtUtils;
import io.jsonwebtoken.Claims;
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

import java.util.Date;
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

    /**
     * 用户登录
     *
     * @param dto     登录信息（用户名+密码）
     * @param request HttpServletRequest，用于获取IP和User-Agent
     * @return JWT token
     */
    @Override
    public String login(LoginDTO dto, HttpServletRequest request) {
        // 验证用户名密码
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 获取登录用户信息
        LoginUserBO bo = (LoginUserBO) authenticate.getPrincipal();
        String userId = bo.getId();
        String username = bo.getUsername();

        // 收集客户端信息
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String device = parseDevice(userAgent);
        String browser = parseBrowser(userAgent);

        // 生成短 sessionId（Redis key）
        String sessionId = JwtUtils.getUUID().substring(0, 8);

        // 封装 JWT subject（JSON 格式）
        LoginUserSessionBO loginUserSessionBO = LoginUserSessionBO.builder()
                .userId(userId)
                .username(username)
                .ip(ip)
                .device(device)
                .browser(browser)
                .sessionId(sessionId)
                .loginTime(new Date().toString())
                .build();
        String subjectJson = JacksonUtils.toJsonString(loginUserSessionBO);

        // 生成 JWT token
        String token = JwtUtils.createJWT(subjectJson);

        // 存 Redis 会话
        String redisKey = RedisKeyPrefixEnum.SYSTEM_NAME.getPrefix() + RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
        redisTemplate.opsForValue().set(redisKey, JacksonUtils.toJsonString(bo));
        redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);

        // 用户维度存储 sessionId（支持多设备）
        String userKey = RedisKeyPrefixEnum.SYSTEM_NAME.getPrefix() + RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;
        redisTemplate.opsForSet().add(userKey, sessionId);

        log.info("用户 {} 登录成功，sessionId={}", username, sessionId);

        return token;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        // 1. 获取请求头中的 token
        String token = request.getHeader("Authorization");
        AssertUtils.isTrue(StringUtils.isBlank(token), "未找到 token");
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            // 2. 解析 JWT
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            LoginUserSessionBO bo = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

            String sessionId = bo.getSessionId();
            String userId = bo.getUserId();

            // 3. 删除 Redis 中的会话
            String redisKey = RedisKeyPrefixEnum.SYSTEM_NAME.getPrefix() + RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
            redisTemplate.delete(redisKey);

            // 4. 删除用户维度的 sessionId
            String userKey = RedisKeyPrefixEnum.SYSTEM_NAME.getPrefix() + RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;
            redisTemplate.opsForSet().remove(userKey, sessionId);

            // 5. 清理 SecurityContext
            SecurityContextHolder.clearContext();

            log.info("用户 {} 登出成功，sessionId={}", bo.getUsername(), sessionId);
            return true;
        } catch (Exception e) {
            log.error("登出失败，原因：{}", e.getMessage(), e);
            throw new OneselfException("登出失败，请重试");
        }
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
