package com.oneself.model.vo;

import com.oneself.model.enums.SexEnum;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.enums.UserTypeEnum;
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
@Schema(name = "UserVO", description = "用户信息数据传输对象")
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private String id;

    @Schema(description = "用户名")
    private String username;

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

    @Schema(description = "所属部门ID")
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
}