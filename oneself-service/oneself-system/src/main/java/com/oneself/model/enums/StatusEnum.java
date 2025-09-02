package com.oneself.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    NORMAL(0, "禁用"),
    LOCKED(1, "启用");

    private final Integer code;
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (StatusEnum status : StatusEnum.values()) {
            if (String.valueOf(status.code).equals(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("StatusEnum 的值无效: " + value);
    }
}
