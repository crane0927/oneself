package com.oneself.system.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.common.infra.jdbc.model.pojo.BasePojo;
import com.oneself.system.model.enums.SexEnum;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.common.feature.security.model.enums.UserTypeEnum;
import lombok.*;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.pojo
 * className User
 * description 用户表
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 性别(0-未知,1-男,2-女)
     */
    @TableField("sex")
    private SexEnum sex;

    /**
     * 用户类型(0-管理员,1-普通用户)
     */
    @TableField("type")
    private UserTypeEnum type;

    /**
     * 所属部门ID
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}
