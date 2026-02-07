package com.oneself.common.core.model.enums;

import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.model.enums
 * enumName BranchTypeEnum
 * description 分支类型枚举
 * version 1.0
 */
@Getter
public enum BranchTypeEnum {
    BRANCH("分支"),
    TAG("标签");

    private final String description;

    BranchTypeEnum(String description) {
        this.description = description;
    }

    public static BranchTypeEnum fromDescription(String description) {
        for (BranchTypeEnum status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的描述: " + description);
    }
}
