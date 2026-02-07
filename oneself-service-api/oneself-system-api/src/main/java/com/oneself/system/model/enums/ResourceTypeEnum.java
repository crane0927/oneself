package com.oneself.system.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.enums
 * enumName ResourceTypeEnum
 * description 
 * version 1.0
 */
@Getter
@Schema(name = "ResourceTypeEnum", description = "资源类型枚举")
public enum ResourceTypeEnum {
    @Schema(description = "菜单")
    MENU(1, "菜单"),
    @Schema(description = "按钮")
    BUTTON(2, "按钮"),
    @Schema(description = "接口")
    API(3, "接口");
    private final int type;
    private final String desc;

    ResourceTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
