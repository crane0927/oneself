package com.oneself.model.vo;

import com.oneself.annotation.Sensitive;
import com.oneself.model.enums.SexEnum;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.enums.TypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.user.model.vo
 * className UserVO
 * description 用户信息
 * version 1.0
 */
@Data
@Schema(description = "用户信息 VO")
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户 ID")
    private String id;
    @Schema(description = "部门 ID")
    private String deptId;
    @Schema(description = "用户登录名")
    private String loginName;
    @Schema(description = "用户名")
    private String username;
    @Sensitive
    @Schema(description = "用户密码")
    private String password;
    @Schema(description = "性别")
    private SexEnum sex;
    @Schema(description = "用户类型")
    private TypeEnum userType;
    @Schema(description = "用户状态")
    private StatusEnum status;
    @Schema(description = "头像路径")
    private String avatar;
    @Schema(description = "邮箱地址")
    private String email;
    @Schema(description = "手机号码")
    private String phone;
    @Schema(description = "最后登录 IP")
    private String lastLoginIp;
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "修改人")
    private String updateBy;
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
