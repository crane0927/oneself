package com.oneself.user.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.enums
 * enumName TypeEnum
 * description 用户类型枚举
 * version 1.0
 */
@Getter
public enum TypeEnum {
    ADMIN(1, "管理员"),
    NORMAL(2, "普通用户");

    private final Integer code;
    private final String desc;

    TypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
