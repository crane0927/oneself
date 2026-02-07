package com.oneself.system.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuhuan
 * date 2025/9/2
 * packageName com.oneself.model.pojo
 * className RolePermission
 * description 角色权限关联表
 * version 1.0
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermission extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private String permId;
}
