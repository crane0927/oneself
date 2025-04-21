package com.oneself.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.model.enums
 * enumName ChatTypeEnum
 * description 会话类型枚举
 * version 1.0
 */
@Getter
public enum ChatTypeEnum {
    CHAT(1, "对话"),
    SETVICE(2, "服务"),
    PDF(3, "PDF");

    private final Integer code;
    private final String desc;

    ChatTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
