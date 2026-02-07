package com.oneself.system.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuhuan
 * date 2025/9/2
 * packageName com.oneself.role.model.pojo
 * className Role
 * description 角色表
 * version 1.0
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

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
     * 父角色ID（用于角色继承，RBAC1）
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}
