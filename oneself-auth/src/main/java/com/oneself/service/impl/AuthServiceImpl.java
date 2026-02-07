package com.oneself.service.impl;

import com.oneself.common.feature.security.config.RsaKeyConfig;
import com.oneself.common.core.exception.OneselfException;
import com.oneself.common.core.exception.OneselfLoginException;
import com.oneself.common.feature.security.model.bo.JwtSessionBO;
import com.oneself.common.feature.security.model.bo.LoginUserBO;
import com.oneself.model.dto.LoginDTO;
import com.oneself.common.infra.redis.model.enums.RedisKeyPrefixEnum;
import com.oneself.model.vo.CaptchaVO;
import com.oneself.service.AuthService;
import com.oneself.service.CaptchaService;
import com.oneself.common.core.utils.JacksonUtils;
import com.oneself.common.feature.security.utils.JwtUtils;
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

    // 登录失败次数限制配置
    private static final int MAX_LOGIN_FAILURE_COUNT = 5; // 最大失败次数
    private static final long LOCK_DURATION_MINUTES = 30; // 锁定时长（分钟）

    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final RsaKeyConfig rsaKeyConfig;
    private final HttpServletRequest request;
    private final CaptchaService captchaService;


    @Override
    public String login(LoginDTO dto, HttpServletRequest request) {
        Assert.notNull(dto, "请求参数不能为空");
        String username = dto.getUsername();

        try {
            // 1. 检查账户是否被锁定（在验证码验证之前检查，避免浪费验证码）
            checkAccountLocked(username);

            // 2. 验证验证码
            if (StringUtils.isBlank(dto.getCaptchaId()) || StringUtils.isBlank(dto.getCaptchaCode())) {
                log.warn("用户 {} 登录失败：验证码为空", username);
                recordLoginFailure(username);
                throw new OneselfLoginException("验证码不能为空");
            }
            if (!captchaService.validateCaptcha(dto.getCaptchaId(), dto.getCaptchaCode())) {
                log.warn("用户 {} 登录失败：验证码错误，captchaId={}", username, dto.getCaptchaId());
                recordLoginFailure(username);
                throw new OneselfLoginException("验证码错误或已过期");
            }

            // 3. RSA 私钥解密
            String password;
            try {
                password = rsaKeyConfig.decrypt(dto.getPassword());
                log.debug("用户 {} 密码解密成功", username);
            } catch (Exception e) {
                log.error("用户 {} 密码解密失败，ip={}", username, getClientIp(request), e);
                recordLoginFailure(username);
                throw new OneselfLoginException("密码解密失败");
            }

            // 4. Spring Security 验证用户名密码
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authenticate;
            try {
                authenticate = authenticationManager.authenticate(authenticationToken);
                log.debug("用户 {} 身份验证成功", username);
            } catch (org.springframework.security.core.AuthenticationException e) {
                String ip = getClientIp(request);
                log.warn("用户 {} 身份验证失败，ip={}, error={}", username, ip, e.getMessage(), e);
                recordLoginFailure(username);
                throw new OneselfLoginException("用户名或密码错误", e);
            }

            if (Objects.isNull(authenticate)) {
                String ip = getClientIp(request);
                log.warn("用户 {} 身份验证返回空，ip={}", username, ip);
                recordLoginFailure(username);
                throw new OneselfLoginException("用户名或密码错误");
            }

        LoginUserBO bo = (LoginUserBO) authenticate.getPrincipal();
        String userId = bo.getId();
        String authenticatedUsername = bo.getUsername();

        // 5. 登录成功，清除登录失败次数
        clearLoginFailureCount(authenticatedUsername);

        // 6. 收集客户端信息
        String ip = getClientIp(request);
        String device = parseDevice();
        String browser = parseBrowser();
        log.debug("用户 {} 登录客户端信息，ip={}, device={}, browser={}", authenticatedUsername, ip, device, browser);

        // 7. 生成 sessionId
        String sessionId = JwtUtils.getUUID().substring(0, 8);

        // 8. 构建 JWT subject
        JwtSessionBO jwtSessionBO = JwtSessionBO.builder()
                .userId(userId)
                .username(authenticatedUsername)
                .ip(ip)
                .device(device)
                .browser(browser)
                .sessionId(sessionId)
                .loginTime(System.currentTimeMillis())
                .build();
        String subjectJson = JacksonUtils.toJsonString(jwtSessionBO);

        // 9. 生成 JWT token
        String token = JwtUtils.createJWT(subjectJson);

        // 10. 存 Redis 会话 (String + TTL)
        String redisKey = buildLoginKey(sessionId);
        long ttlSeconds = 3600; // 1 小时
        bo.setPassword(null);
        redisTemplate.opsForValue().set(redisKey, JacksonUtils.toJsonString(bo), ttlSeconds, TimeUnit.SECONDS);

        // 11. 用户维度存储 sessionId（SortedSet + expireAt）
        String userKey = buildUserSessionsKey(userId);
        long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttlSeconds);
        redisTemplate.opsForZSet().add(userKey, sessionId, expireAt);

        // 12. 可选：清理过期 sessionId
        redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

        log.info("用户 {} 登录成功，userId={}, sessionId={}, ip={}, device={}, browser={}, ttl={}s",
                authenticatedUsername, userId, sessionId, ip, device, browser, ttlSeconds);
        return token;
        } catch (OneselfLoginException e) {
            // 登录相关异常直接抛出
            String ip = getClientIp(request);
            log.warn("用户 {} 登录失败，ip={}, reason={}", username, ip, e.getMessage());
            throw e;
        } catch (Exception e) {
            // 其他异常记录失败次数
            String ip = getClientIp(request);
            log.error("用户 {} 登录发生异常，ip={}", username, ip, e);
            recordLoginFailure(username);
            throw new OneselfLoginException("登录失败，请重试");
        }
    }

    @Override
    public CaptchaVO generateCaptcha() {
        return captchaService.generateCaptcha();
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        String ip = getClientIp(request);
        // 获取 token
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            log.warn("登出请求失败：未找到 token，ip={}", ip);
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
            String username = bo.getUsername();

            // 删除 Redis 会话
            redisTemplate.delete(buildLoginKey(sessionId));

            // 删除 SortedSet 中的 sessionId
            String userKey = buildUserSessionsKey(userId);
            redisTemplate.opsForZSet().remove(userKey, sessionId);

            // 清理 SecurityContext
            SecurityContextHolder.clearContext();

            log.info("用户 {} 登出成功，userId={}, sessionId={}, ip={}", username, userId, sessionId, ip);
            return true;
        } catch (Exception e) {
            log.error("登出失败，ip={}", ip, e);
            throw new OneselfException("登出失败，请重试");
        }
    }

    @Override
    public String refresh(HttpServletRequest request) {
        String ip = getClientIp(request);
        // 1. 获取旧 token
        String oldToken = request.getHeader("Authorization");
        if (StringUtils.isBlank(oldToken)) {
            log.warn("Token刷新请求失败：未找到 token，ip={}", ip);
            throw new OneselfException("未找到 token");
        }
        if (oldToken.startsWith("Bearer ")) {
            oldToken = oldToken.substring(7);
        }

        try {
            // 2. 解析旧 token
            var claims = JwtUtils.parseJWT(oldToken);
            String subject = claims.getSubject();
            JwtSessionBO oldSessionBO = JacksonUtils.fromJson(subject, JwtSessionBO.class);

            String sessionId = oldSessionBO.getSessionId();
            String userId = oldSessionBO.getUserId();
            String username = oldSessionBO.getUsername();

            log.debug("用户 {} Token刷新请求，userId={}, sessionId={}, ip={}", username, userId, sessionId, ip);

            // 3. 检查 Redis 会话是否存在
            String redisKey = buildLoginKey(sessionId);
            String sessionJson = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isBlank(sessionJson)) {
                log.warn("用户 {} Token刷新失败：会话已过期，sessionId={}, ip={}", username, sessionId, ip);
                throw new OneselfException("会话已过期，请重新登录");
            }

            // 4. 检查绝对过期时间（7天）
            long absoluteExpireMillis = TimeUnit.DAYS.toMillis(7);
            if (System.currentTimeMillis() - oldSessionBO.getLoginTime() > absoluteExpireMillis) {
                // 超过绝对过期时间，删除会话
                redisTemplate.delete(redisKey);
                String userKey = buildUserSessionsKey(userId);
                redisTemplate.opsForZSet().remove(userKey, sessionId);
                log.warn("用户 {} Token刷新失败：会话已超过最大有效期，sessionId={}, ip={}", username, sessionId, ip);
                throw new OneselfException("会话已超过最大有效期，请重新登录");
            }

            // 5. 获取用户会话信息
            LoginUserBO loginUserBO = JacksonUtils.fromJson(sessionJson, LoginUserBO.class);

            // 6. 更新客户端信息（IP、设备、浏览器可能变化）
            String newIp = getClientIp(request);
            String device = parseDevice();
            String browser = parseBrowser();
            if (!ip.equals(newIp)) {
                log.debug("用户 {} Token刷新时IP发生变化，oldIp={}, newIp={}", username, ip, newIp);
            }

            // 7. 构建新的 JWT subject（保持相同的 sessionId 和 loginTime）
            JwtSessionBO newSessionBO = JwtSessionBO.builder()
                    .userId(userId)
                    .username(username)
                    .ip(newIp)
                    .device(device)
                    .browser(browser)
                    .sessionId(sessionId)
                    .loginTime(oldSessionBO.getLoginTime()) // 保持原始登录时间
                    .build();
            String newSubjectJson = JacksonUtils.toJsonString(newSessionBO);

            // 8. 生成新的 JWT token
            String newToken = JwtUtils.createJWT(newSubjectJson);

            // 9. 更新 Redis 会话过期时间（滑动过期，重新设置为1小时）
            long ttlSeconds = 3600; // 1 小时
            redisTemplate.opsForValue().set(redisKey, sessionJson, ttlSeconds, TimeUnit.SECONDS);

            // 10. 更新用户维度存储的 sessionId 过期时间
            String userKey = buildUserSessionsKey(userId);
            long expireAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(ttlSeconds);
            redisTemplate.opsForZSet().add(userKey, sessionId, expireAt);

            // 11. 清理过期 sessionId
            redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

            log.info("用户 {} Token刷新成功，userId={}, sessionId={}, ip={}, device={}, browser={}",
                    username, userId, sessionId, newIp, device, browser);
            return newToken;
        } catch (OneselfException e) {
            log.warn("Token刷新失败，ip={}, error={}", ip, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Token刷新发生异常，ip={}", ip, e);
            throw new OneselfException("Token 刷新失败，请重试");
        }
    }

    // ------------------------ 工具方法 ------------------------

    private String buildLoginKey(String sessionId) {
        return RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
    }

    private String buildUserSessionsKey(String userId) {
        return RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;
    }

    /**
     * 构建登录失败次数 Redis Key
     */
    private String buildLoginFailureKey(String username) {
        return RedisKeyPrefixEnum.LOGIN_FAILURE.getPrefix() + username;
    }

    /**
     * 检查账户是否被锁定
     *
     * @param username 用户名
     * @throws OneselfLoginException 如果账户被锁定
     */
    private void checkAccountLocked(String username) {
        if (StringUtils.isBlank(username)) {
            return;
        }

        String failureKey = buildLoginFailureKey(username);
        String failureCountStr = redisTemplate.opsForValue().get(failureKey);

        if (StringUtils.isNotBlank(failureCountStr)) {
            try {
                int failureCount = Integer.parseInt(failureCountStr);
                if (failureCount >= MAX_LOGIN_FAILURE_COUNT) {
                    // 检查锁定剩余时间
                    Long ttl = redisTemplate.getExpire(failureKey, TimeUnit.MINUTES);
                    if (ttl != null && ttl > 0) {
                        String ip = getClientIp(request);
                        log.warn("用户 {} 尝试登录但账户已被锁定，剩余锁定时间={}分钟，ip={}", username, ttl, ip);
                        throw new OneselfLoginException(
                                String.format("账户已被锁定，请 %d 分钟后再试", ttl));
                    } else {
                        // TTL 已过期，清除失败次数
                        log.debug("用户 {} 账户锁定已过期，清除失败记录", username);
                        redisTemplate.delete(failureKey);
                    }
                }
            } catch (NumberFormatException e) {
                log.warn("解析登录失败次数失败，username={}, value={}", username, failureCountStr, e);
                redisTemplate.delete(failureKey);
            }
        }
    }

    /**
     * 记录登录失败
     *
     * @param username 用户名
     */
    private void recordLoginFailure(String username) {
        if (StringUtils.isBlank(username)) {
            return;
        }

        String failureKey = buildLoginFailureKey(username);
        String failureCountStr = redisTemplate.opsForValue().get(failureKey);

        int failureCount = 1;
        if (StringUtils.isNotBlank(failureCountStr)) {
            try {
                failureCount = Integer.parseInt(failureCountStr) + 1;
            } catch (NumberFormatException e) {
                log.warn("解析登录失败次数失败，username={}, value={}", username, failureCountStr);
                failureCount = 1;
            }
        }

        String ip = getClientIp(request);
        if (failureCount >= MAX_LOGIN_FAILURE_COUNT) {
            // 达到最大失败次数，锁定账户
            redisTemplate.opsForValue().set(failureKey, String.valueOf(failureCount),
                    LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
            log.warn("用户 {} 登录失败次数达到 {} 次，账户已锁定 {} 分钟，ip={}",
                    username, failureCount, LOCK_DURATION_MINUTES, ip);
        } else {
            // 未达到最大失败次数，记录失败次数（30分钟过期，与锁定时长一致）
            redisTemplate.opsForValue().set(failureKey, String.valueOf(failureCount),
                    LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
            log.warn("用户 {} 登录失败，当前失败次数: {}/{}, ip={}",
                    username, failureCount, MAX_LOGIN_FAILURE_COUNT, ip);
        }
    }

    /**
     * 清除登录失败次数
     *
     * @param username 用户名
     */
    private void clearLoginFailureCount(String username) {
        if (StringUtils.isBlank(username)) {
            return;
        }

        String failureKey = buildLoginFailureKey(username);
        redisTemplate.delete(failureKey);
        log.debug("用户 {} 登录成功，已清除登录失败次数", username);
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
