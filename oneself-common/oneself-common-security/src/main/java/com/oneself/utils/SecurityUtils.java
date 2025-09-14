package com.oneself.utils;

import com.oneself.exception.OneselfException;
import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import com.oneself.model.enums.UserTypeEnum;
import com.oneself.model.vo.UserSessionVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liuhuan
 * date 2025/9/12
 * packageName com.oneself.utils
 * className SecurityUtils
 * description
 * version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final HttpServletRequest request;
    private final RedisTemplate<String, String> redisTemplate;

    private static final ThreadLocal<LoginUserSessionBO> USER_HOLDER = new ThreadLocal<>();
    private static final String DEFAULT_USER = "system";

    private static final long SESSION_TIMEOUT_HOURS = 1; // 滑动过期时长
    private static final long RENEW_THRESHOLD_MINUTES = 10; // 滑动续期阈值
    private static final long ABSOLUTE_EXPIRE_DAYS = 7; // 最大绝对过期天数

    // ================== JWT & Session ==================
    public String resolveToken() {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) return null;
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    public LoginUserSessionBO parseAndValidateToken(String token) {
        if (StringUtils.isBlank(token)) return null;
        try {
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            LoginUserSessionBO sessionBO = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

            String sessionId = sessionBO.getSessionId();
            String userId = sessionBO.getUserId();
            String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
            String userKey = RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;

            // 清理已过期 session
            redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

            Long expire = redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            if (expire > 0) {
                long absoluteExpireMillis = TimeUnit.DAYS.toMillis(ABSOLUTE_EXPIRE_DAYS);
                if (System.currentTimeMillis() - sessionBO.getLoginTime() > absoluteExpireMillis) {
                    redisTemplate.delete(redisKey);
                    redisTemplate.opsForZSet().remove(userKey, sessionId);
                    log.warn("用户 [{}] 的 session [{}] 已超过 {} 天，强制过期", userId, sessionId, ABSOLUTE_EXPIRE_DAYS);
                    return null;
                }

                refreshSessionIfNecessary(userId, sessionId, redisKey, userKey);

                USER_HOLDER.set(sessionBO);
                return sessionBO;
            } else {
                redisTemplate.opsForZSet().remove(userKey, sessionId);
            }

        } catch (Exception e) {
            log.warn("token 解析失败", e);
        }
        return null;
    }

    private void refreshSessionIfNecessary(String userId, String sessionId, String redisKey, String userKey) {
        long ttlMinutes = redisTemplate.getExpire(redisKey, TimeUnit.MINUTES);
        if (ttlMinutes > 0 && ttlMinutes < RENEW_THRESHOLD_MINUTES) {
            redisTemplate.expire(redisKey, SESSION_TIMEOUT_HOURS, TimeUnit.HOURS);
            redisTemplate.expire(userKey, SESSION_TIMEOUT_HOURS, TimeUnit.HOURS);

            long newExpireAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(SESSION_TIMEOUT_HOURS);
            redisTemplate.opsForZSet().add(userKey, sessionId, newExpireAt);
            log.debug("用户 [{}] 的 session [{}] 已滑动续期 {} 小时", userId, sessionId, SESSION_TIMEOUT_HOURS);
        }
    }

    public LoginUserSessionBO getCurrentUser() {
        return USER_HOLDER.get();
    }

    public String getCurrentUsername() {
        LoginUserSessionBO user = getCurrentUser();
        return user != null ? user.getUsername() : DEFAULT_USER;
    }

    public void clear() {
        USER_HOLDER.remove();
    }

    // ================== 登录 / 角色 / 权限校验 ==================
    public void checkLogin() {
        LoginUserSessionBO user = getCurrentUser();
        if (user == null) {
            throw new OneselfException("用户未登录或 token 无效");
        }
    }

    public void checkRole(String... requiredRoles) {
        checkLogin();
        UserSessionVO user = loadUserFromRedis(getCurrentUser().getUserId());
        if (UserTypeEnum.ADMIN.equals(user.getType())) return; // 管理员直接放行

        Set<String> userRoles = new HashSet<>(user.getRoleCodes());
        Set<String> needRoles = new HashSet<>(Arrays.asList(requiredRoles));
        if (Collections.disjoint(userRoles, needRoles)) {
            throw new OneselfException("角色权限不足");
        }
    }

    public void checkPermission(String... requiredPerms) {
        checkLogin();
        UserSessionVO user = loadUserFromRedis(getCurrentUser().getUserId());
        if (UserTypeEnum.ADMIN.equals(user.getType())) return; // 管理员直接放行

        Set<String> userPerms = new HashSet<>(user.getPermissionCodes());
        Set<String> needPerms = new HashSet<>(Arrays.asList(requiredPerms));
        if (Collections.disjoint(userPerms, needPerms)) {
            throw new OneselfException("操作权限不足");
        }
    }

    private UserSessionVO loadUserFromRedis(String userId) {
        String userKey = RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;
        String json = redisTemplate.opsForValue().get(userKey);
        return JacksonUtils.fromJson(json, UserSessionVO.class);
    }
}