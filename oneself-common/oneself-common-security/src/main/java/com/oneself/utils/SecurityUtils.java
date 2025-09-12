package com.oneself.utils;

import com.oneself.model.bo.LoginUserSessionBO;
import com.oneself.model.enums.RedisKeyPrefixEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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

    /**
     * 默认续期时长（1 小时）
     */
    private static final long SESSION_TIMEOUT_HOURS = 1;
    /**
     * 触发续期的阈值（剩余 < 10 分钟才续期）
     */
    private static final long RENEW_THRESHOLD_MINUTES = 10;
    /**
     * 最大绝对时长（7 天，必须重新登录）
     */
    private static final long ABSOLUTE_EXPIRE_DAYS = 7;

    /**
     * 从请求头中解析 JWT token
     */
    public String resolveToken() {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token.startsWith("Bearer ") ? token.substring(7) : token;
    }

    /**
     * 解析 JWT token 并校验 Redis 中的会话信息，支持双时效：
     * <ul>
     *   <li>1 小时滑动过期</li>
     *   <li>7 天绝对过期</li>
     * </ul>
     */
    public LoginUserSessionBO parseAndValidateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        try {
            Claims claims = JwtUtils.parseJWT(token);
            String subject = claims.getSubject();
            LoginUserSessionBO sessionBO = JacksonUtils.fromJson(subject, LoginUserSessionBO.class);

            String sessionId = sessionBO.getSessionId();
            String userId = sessionBO.getUserId();
            String redisKey = RedisKeyPrefixEnum.LOGIN_SESSION.getPrefix() + sessionId;
            String userKey = RedisKeyPrefixEnum.LOGIN_USER.getPrefix() + userId;

            // 1. 清理已过期的 sessionId
            redisTemplate.opsForZSet().removeRangeByScore(userKey, 0, System.currentTimeMillis());

            // 2. 校验 Redis 是否存在
            Long expire = redisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            if (expire > 0) {
                // 3. 检查绝对过期时间
                long loginTimeMillis = sessionBO.getLoginTime();
                long absoluteExpireMillis = TimeUnit.DAYS.toMillis(ABSOLUTE_EXPIRE_DAYS);
                if (System.currentTimeMillis() - loginTimeMillis > absoluteExpireMillis) {
                    // 超过绝对时效 -> 强制下线
                    redisTemplate.delete(redisKey);
                    redisTemplate.opsForZSet().remove(userKey, sessionId);
                    log.warn("用户 [{}] 的 session [{}] 已超过 {} 天，强制过期", userId, sessionId, ABSOLUTE_EXPIRE_DAYS);
                    return null;
                }

                // 4. 检查是否需要续期（滑动过期）
                refreshSessionIfNecessary(userId, sessionId, redisKey, userKey);

                USER_HOLDER.set(sessionBO);
                return sessionBO;
            } else {
                // session 已过期，清理
                redisTemplate.opsForZSet().remove(userKey, sessionId);
            }

        } catch (Exception e) {
            log.warn("token 解析失败", e);
        }

        return null;
    }

    /**
     * 滑动续期：如果 TTL 小于阈值，则重置 TTL。
     */
    private void refreshSessionIfNecessary(String userId, String sessionId, String redisKey, String userKey) {
        long ttlMinutes = redisTemplate.getExpire(redisKey, TimeUnit.MINUTES);

        if (ttlMinutes > 0 && ttlMinutes < RENEW_THRESHOLD_MINUTES) {
            // 续期 1 小时
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
}