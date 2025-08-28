package com.oneself.script.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/8/28
 * packageName com.oneself.script.model.enums
 * enumName ExecuteModeEnum
 * description 执行模式枚举
 * version 1.0
 */
@Getter
@Schema(description = "执行模式枚举")
public enum ExecuteModeEnum {

    @Schema(description = "本地执行")
    LOCAL("本地执行"),

    @Schema(description = "远程执行")
    REMOTE("远程执行");

    private final String description;

    ExecuteModeEnum(String description) {
        this.description = description;
    }

    public static ExecuteModeEnum fromDescription(String desc) {
        for (ExecuteModeEnum mode : values()) {
            if (mode.description.equals(desc)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("未知的执行模式描述: " + desc);
    }
}
