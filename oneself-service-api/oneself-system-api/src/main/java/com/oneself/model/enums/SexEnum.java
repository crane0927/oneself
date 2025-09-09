package com.oneself.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "SexEnum", description = "性别枚举")
public enum SexEnum {
    @Schema(description = "未知")
    UNKNOWN(0, "未知"),
    @Schema(description = "男")
    MALE(1, "男"),
    @Schema(description = "女")
    FEMALE(2, "女");

    private final Integer code;
    private final String desc;

    SexEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
