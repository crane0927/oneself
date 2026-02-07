package com.oneself.common.feature.security.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "StatusEnum", description = "状态枚举")
public enum StatusEnum {
    @Schema(description = "正常")
    NORMAL(0, "正常"),
    @Schema(description = "锁定")
    LOCKED(1, "锁定");

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
