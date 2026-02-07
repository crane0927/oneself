package com.oneself.system.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.model.enums
 * enumName ConfigurationTypeEnum
 * description
 * version 1.0
 */
@Getter
@Schema(name = "ConfigurationTypeEnum", description = "参数配置类型枚举")
public enum ConfigurationTypeEnum {
    @Schema(description = "系统参数")
    SYSTEM(0, "系统参数"),
    @Schema(description = "业务参数")
    BUSINESS(1, "业务参数");

    private final Integer code;
    private final String desc;

    ConfigurationTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ConfigurationTypeEnum fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (ConfigurationTypeEnum status : ConfigurationTypeEnum.values()) {
            if (String.valueOf(status.code).equals(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("ConfigurationTypeEnum 的值无效: " + value);
    }
}
