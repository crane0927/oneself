package com.oneself.model.dto;

import com.oneself.model.enums.SexEnum;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.dto
 * className UserQueryDTO
 * description
 * version 1.0
 */
@Data
@Schema(name = "UserQueryDTO", description = "用户查询条件对象")
public class UserQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "邮箱（模糊查询）")
    private String email;

    @Schema(description = "手机号（模糊查询）")
    private String phone;

    @Schema(description = "真实姓名（模糊查询）")
    private String realName;

    @Schema(description = "性别")
    private SexEnum sex;

    @Schema(description = "用户类型")
    private UserTypeEnum type;

    @Schema(description = "所属部门ID")
    private String deptId;

    @Schema(description = "状态")
    private StatusEnum status;
}
