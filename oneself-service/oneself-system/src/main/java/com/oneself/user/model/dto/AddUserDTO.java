package com.oneself.user.model.dto;

import com.oneself.annotation.Sensitive;
import com.oneself.user.model.enums.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.dto
 * className AddUserDTO
 * description 新增用户 DTO
 * version 1.0
 */
@Data
public class AddUserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "部门 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deptId;
    @Schema(description = "登录名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginName;
    @Schema(description = "用户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Sensitive
    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED)
    private SexEnum sex;
    @Schema(description = "头像文件路径", requiredMode = Schema.RequiredMode.REQUIRED)
    private String avatar;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "手机号码")
    private String phone;
    @Schema(description = "描述")
    private String remark;
}
