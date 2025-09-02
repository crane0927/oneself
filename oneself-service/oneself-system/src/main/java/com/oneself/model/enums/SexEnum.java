package com.oneself.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.enums
 * enumName SexEnum
 * description 性别枚举
 * version 1.0
 */
@Getter
public enum SexEnum {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer code;
    private final String desc;

    SexEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
