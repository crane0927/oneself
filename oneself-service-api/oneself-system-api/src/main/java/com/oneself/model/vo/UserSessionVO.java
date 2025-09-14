package com.oneself.model.vo;

import com.oneself.annotation.Sensitive;
import com.oneself.model.enums.DesensitizedTypeEnum;
import com.oneself.model.enums.SexEnum;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.model.vo
 * className UserSessionVO
 * description
 * version 1.0
 */
@Data
@Schema(name = "UserSessionVO", description = "登录用户信息数据传输对象")
public class UserSessionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户 ID")
    private String id;

    @Schema(description = "用户名")
    private String username;

    @Sensitive(value = DesensitizedTypeEnum.PASSWORD)
    @Schema(description = "密码")
    private transient String password;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别(0-未知,1-男,2-女)")
    private SexEnum sex;

    @Schema(description = "用户类型(0-管理员,1-普通用户)")
    private UserTypeEnum type;

    @Schema(description = "所属部门 ID")
    private String deptId;

    @Schema(description = "状态")
    private StatusEnum status;

    @Schema(description = "角色 Code 列表")
    private List<String> roleCodes;

    @Schema(description = "权限 Code 列表")
    private List<String> permissionCodes;
}
