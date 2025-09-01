package com.oneself.script.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/8/29
 * packageName com.oneself.script.model.enums
 * enumName LanguageEnum
 * description 脚本语言枚举
 * version 1.0
 */
@Getter
public enum LanguageEnum {
    PYTHON("Python"),
    SHELL("Shell");
    private final String description;

    LanguageEnum(String description) {
        this.description = description;
    }
}
