package com.oneself.common.infra.redis.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.model.enums
 * enumName RedisKeyPrefixEnum
 * description Redis 键前缀枚举
 * version 1.0
 */
@Getter
public enum RedisKeyPrefixEnum {
    SYSTEM_NAME("oneself:", "系统名称"),
    LOGIN_SESSION("oneself:login:session:", "登录会话"),
    LOGIN_USER("oneself:login:user:", "登录用户"),
    CAPTCHA("oneself:captcha:", "验证码"),
    LOGIN_FAILURE("oneself:login:failure:", "登录失败次数");

    private final String prefix;
    private final String desc;

    RedisKeyPrefixEnum(String prefix, String desc) {
        this.prefix = prefix;
        this.desc = desc;
    }
}
