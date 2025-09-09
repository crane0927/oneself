package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.model.dto
 * className LoginUserDTO
 * description
 * version 1.0
 */
@Data
@Schema(name = "LoginUserDTO", description = "登录用户数据传输对象")
public class LoginUserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", example = "admin")
    private String username;
    @Schema(description = "密码", example = "password123")
    private String password;
}
