package com.oneself.system.model.dto;

import com.oneself.system.model.enums.SexEnum;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.common.feature.security.model.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息 DTO（服务间传输，宪法原则 13：API 模块仅暴露 DTO）
 *
 * @author liuhuan
 */
@Data
@Schema(name = "UserDTO", description = "用户信息数据传输对象")
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户 ID")
    private String id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

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

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改人")
    private String updateBy;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "角色 Code 列表")
    private Set<String> roleCodes = new HashSet<>();

    @Schema(description = "权限 Code 列表")
    private Set<String> permissionCodes = new HashSet<>();
}
