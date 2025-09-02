package com.oneself.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.model.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @author liuhuan
 * date 2025/9/2
 * packageName com.oneself.role.model.pojo
 * className Role
 * description 角色表
 * version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
public class Role extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private UUID id;

    /**
     * 角色编码
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}
