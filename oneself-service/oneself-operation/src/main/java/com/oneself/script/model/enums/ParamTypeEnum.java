package com.oneself.script.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/8/28
 * packageName com.oneself.script.model.enums
 * enumName ParamTypeEnum
 * description 参数类型枚举
 * version 1.0
 */
@Getter
@Schema(description = "参数类型枚举")
public enum ParamTypeEnum {

    @Schema(description = "字符串")
    STRING("字符串"),

    @Schema(description = "长文本")
    TEXT("文本"),

    @Schema(description = "日期")
    DATE("日期"),

    @Schema(description = "数字")
    NUMBER("数字"),

    @Schema(description = "布尔值")
    BOOLEAN("布尔"),

    @Schema(description = "对象")
    OBJECT("对象"),

    @Schema(description = "数组（多值）")
    ARRAY("数组"),

    @Schema(description = "文件")
    FILE("文件");

    private final String description;

    ParamTypeEnum(String description) {
        this.description = description;
    }

    public static ParamTypeEnum fromDescription(String desc) {
        for (ParamTypeEnum type : values()) {
            if (type.description.equals(desc)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的参数类型描述: " + desc);
    }
}
