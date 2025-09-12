package com.oneself.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.model.enums
 * enumName RedisKeyPrefixEnum
 * description
 * version 1.0
 */
@Getter
@Schema(description = "Redis 键前缀枚举")
public enum RedisKeyPrefixEnum {
    SYSTEM_NAME("oneself:", "系统名称"),
    LOGIN_SESSION("oneself:login:session:", "登录会话"),
    LOGIN_USER("oneself:login:user:", "登录用户");

    private final String prefix;
    private final String desc;

    RedisKeyPrefixEnum(String prefix, String desc) {
        this.prefix = prefix;
        this.desc = desc;
    }
}
