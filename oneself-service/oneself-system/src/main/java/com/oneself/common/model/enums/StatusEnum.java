package com.oneself.common.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.common.model.enums
 * enumName StatusEnum
 * description 状态枚举
 * version 1.0
 */
@Getter
public enum StatusEnum {
    NORMAL(1, "正常"),
    LOCKED(2, "锁定");

    private final Integer code;
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
