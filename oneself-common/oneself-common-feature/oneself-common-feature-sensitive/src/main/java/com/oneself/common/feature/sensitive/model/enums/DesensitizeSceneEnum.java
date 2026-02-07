package com.oneself.common.feature.sensitive.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.model.enums
 * enumName DesensitizeSceneEnum
 * description 脱敏场景枚举
 * version 1.0
 */
@Getter
public enum DesensitizeSceneEnum {
    LOG_ONLY(1, "仅日志脱敏"),
    RESPONSE_ONLY(2, "仅前端响应脱敏"),
    BOTH(3, "日志和前端响应均脱敏");

    private final Integer code;
    private final String desc;
    DesensitizeSceneEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
