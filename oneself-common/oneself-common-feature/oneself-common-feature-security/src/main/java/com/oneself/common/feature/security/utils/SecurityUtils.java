package com.oneself.common.feature.security.utils;

import com.oneself.common.core.exception.OneselfException;
import com.oneself.common.core.utils.JacksonUtils;
import com.oneself.common.feature.security.model.bo.JwtSessionBO;
import com.oneself.common.feature.security.model.bo.LoginUserBO;
import com.oneself.common.infra.redis.model.enums.RedisKeyPrefixEnum;
import com.oneself.common.feature.security.model.enums.UserTypeEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liuhuan
 * date 2025/9/12
 * packageName com.oneself.utils
 * className SecurityUtils
 * description
 * Security 工具类，提供：
 * - JWT 解析 / 会话管理
 * - ThreadLocal 缓存当前用户
 * - 角色 / 权限校验
 * version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final HttpServletRequest request;
    private final RedisTemplate<String, String> redisTemplate;

    private static final ThreadLocal<JwtSessionBO> USER_HOLDER = new ThreadLocal<>();
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

    public JwtSessionBO parseAndValidateToken(String token) {
        if (StringUtils.isBlank(token)) return null;

        try {
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            JwtSessionBO sessionBO = JacksonUtils.fromJson(subject, JwtSessionBO.class);

            String sessionId = sessionBO.getSessionId();
            String userId = sessionBO.getUserId();
            String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
            String userKey = RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;

            // 清理已过期 session（ZSet 中的老数据）
            redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

            Long expire = redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            if (expire > 0) {
                // 绝对过期检查
                long absoluteExpireMillis = TimeUnit.DAYS.toMillis(ABSOLUTE_EXPIRE_DAYS);
                if (System.currentTimeMillis() - sessionBO.getLoginTime() > absoluteExpireMillis) {
                    redisTemplate.delete(redisKey);
                    redisTemplate.opsForZSet().remove(userKey, sessionId);
                    log.warn("用户 [{}] 的 session [{}] 已超过 {} 天，强制过期", userId, sessionId, ABSOLUTE_EXPIRE_DAYS);
                    return null;
                }

                // 滑动过期：必要时续期
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

    public JwtSessionBO getCurrentUser() {
        return USER_HOLDER.get();
    }

    public void setCurrentUser(JwtSessionBO user) {
        USER_HOLDER.set(user);
    }

    public String getCurrentUsername() {
        JwtSessionBO user = getCurrentUser();
        return user != null ? user.getUsername() : DEFAULT_USER;
    }

    public void clear() {
        USER_HOLDER.remove();
    }

    // ================== 登录 / 角色 / 权限校验 ==================
    public void checkLogin() {
        if (getCurrentUser() == null) {
            throw new OneselfException("用户未登录或 token 无效");
        }
    }

    public void checkRole(String[] requiredRoles, boolean strict) {
        checkLogin();
        LoginUserBO user = loadUserFromRedis(getCurrentUser().getSessionId());
        if (user == null) {
            if (strict) throw new OneselfException("用户信息不存在");
            return;
        }

        if (UserTypeEnum.ADMIN.equals(user.getType())) return; // 管理员直接放行

        Set<String> userRoles = Optional.ofNullable(user.getRoleCodes()).orElse(Collections.emptySet());
        Set<String> needRoles = new HashSet<>(Arrays.asList(requiredRoles));
        if (Collections.disjoint(userRoles, needRoles) && strict) {
            throw new OneselfException("角色权限不足");
        }
    }

    public void checkPermission(String[] requiredPerms, boolean strict) {
        checkLogin();
        LoginUserBO user = loadUserFromRedis(getCurrentUser().getSessionId());
        if (user == null) {
            if (strict) throw new OneselfException("用户信息不存在");
            return;
        }

        if (UserTypeEnum.ADMIN.equals(user.getType())) return; // 管理员直接放行

        Set<String> userPerms = Optional.ofNullable(user.getPermissionCodes()).orElse(Collections.emptySet());
        Set<String> needPerms = new HashSet<>(Arrays.asList(requiredPerms));
        if (Collections.disjoint(userPerms, needPerms) && strict) {
            throw new OneselfException("操作权限不足");
        }
    }

    private LoginUserBO loadUserFromRedis(String userId) {
        String sessionKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + userId;
        String json = redisTemplate.opsForValue().get(sessionKey);
        return JacksonUtils.fromJson(json, LoginUserBO.class);
    }
}