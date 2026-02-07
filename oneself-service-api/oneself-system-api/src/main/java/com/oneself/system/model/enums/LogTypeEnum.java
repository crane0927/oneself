package com.oneself.system.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.model.enums
 * enumName LogTypeEnum
 * description 日志类型枚举
 * version 1.0
 */
@Getter
@Schema(name = "LogTypeEnum", description = "日志类型枚举")
public enum LogTypeEnum {
    LOGIN(1, "登录日志"),
    LOGOUT(2, "登出日志"),
    INSERT(3, "新增日志"),
    UPDATE(4, "修改日志"),
    DELETE(5, "删除日志"),
    SELECT(6, "查询日志"),
    DOWNLOAD(7, "下载日志"),
    UPLOAD(8, "上传日志"),
    OTHER(9, "其它日志");

    private final Integer code;
    private final String desc;

    LogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
