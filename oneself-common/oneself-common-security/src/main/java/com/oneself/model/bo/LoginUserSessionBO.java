package com.oneself.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.model.bo
 * className LoginUserSessionBO
 * description
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录用户会话信息 BO")
public class LoginUserSessionBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户 ID")
    private String userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录 IP")
    private String ip;

    @Schema(description = "登录设备类型")
    private String device;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "会话 ID，短唯 一ID，作为 Redis key")
    private String sessionId;

    @Schema(description = "登录时间，毫秒时间戳")
    private Long loginTime;
}
