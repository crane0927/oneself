package com.oneself.script.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.script.model.enums
 * enumName ScriptStatusEnum
 * description 脚本状态枚举
 * version 1.0
 */
@Getter
public enum ScriptStatusEnum {
    DRAFT("草稿"),
    ACTIVE("启用"),
    DEPRECATED("废弃");

    private final String description;

    ScriptStatusEnum(String description) {
        this.description = description;
    }
}
