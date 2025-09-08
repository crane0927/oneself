package com.oneself.model.dto;

import com.oneself.model.enums.SexEnum;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.dto
 * className UserDTO
 * description 新增用户 DTO
 * version 1.0
 */
@Data
@Schema(name = "UserDTO", description = "用户数据传输对象")
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别(0-未知,1-男,2-女)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "性别不能为空")
    private SexEnum sex;

    @Schema(description = "用户类型(0-管理员,1-普通用户)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户类型不能为空")
    private UserTypeEnum type;

    @Schema(description = "所属部门ID")
    private String deptId;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private StatusEnum status;
}