package com.oneself.common.model.enums;

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
    NORMAL(1, "正常"),
    LOCKED(2, "锁定");

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
