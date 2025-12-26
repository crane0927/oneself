package com.oneself.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.model.enums
 * enumName ConstraintTypeEnum
 * description 约束类型枚举（RBAC2）
 * version 1.0
 */
@Getter
@Schema(name = "ConstraintTypeEnum", description = "约束类型枚举")
public enum ConstraintTypeEnum {
    @Schema(description = "角色互斥")
    ROLE_MUTEX("ROLE_MUTEX", "角色互斥"),
    @Schema(description = "权限互斥")
    PERM_MUTEX("PERM_MUTEX", "权限互斥"),
    @Schema(description = "基数约束")
    CARDINALITY("CARDINALITY", "基数约束"),
    @Schema(description = "先决条件")
    PREREQUISITE("PREREQUISITE", "先决条件");

    private final String code;
    private final String desc;

    ConstraintTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ConstraintTypeEnum fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (ConstraintTypeEnum type : ConstraintTypeEnum.values()) {
            if (type.code.equals(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("ConstraintTypeEnum 的值无效: " + value);
    }
}

