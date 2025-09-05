package com.oneself.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.enums
 * enumName UserTypeEnum
 * description 用户类型枚举
 * version 1.0
 */
@Getter
@Schema(name = "UserTypeEnum", description = "用户类型枚举")
public enum UserTypeEnum {
    @Schema(description = "管理员")
    ADMIN(0, "管理员"),
    @Schema(description = "普通用户")
    NORMAL(1, "普通用户");

    private final Integer code;
    private final String desc;

    UserTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
