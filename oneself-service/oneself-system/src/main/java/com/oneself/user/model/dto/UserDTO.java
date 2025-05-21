package com.oneself.user.model.dto;

import com.oneself.annotation.Sensitive;
import com.oneself.user.model.enums.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "部门 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部门 ID 不能为空")
    @Positive(message = "部门 ID 必须是正数")
    private Long deptId;

    @Schema(description = "登录名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "登录名称不能为空")
    @Size(min = 4, max = 20, message = "登录名称长度必须在 4 到 20 个字符之间")
    private String loginName;

    @Schema(description = "用户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名称不能为空")
    @Size(max = 50, message = "用户名称长度不能超过 50 个字符")
    private String username;

    @Sensitive // 自定义注解，用于敏感信息处理
    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "用户密码长度必须在 6 到 20 个字符之间")
    private String password;

    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "性别不能为空")
    private SexEnum sex;

    @Schema(description = "头像文件路径", nullable = true)
    @Pattern(
            regexp = "^$|^(https?://)([\\w\\-./]+)\\.(jpg|jpeg|png|gif)$",
            message = "头像文件路径必须是有效的图片 URL"
    )
    private String avatar;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号码")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    @Schema(description = "描述")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String remark;
}