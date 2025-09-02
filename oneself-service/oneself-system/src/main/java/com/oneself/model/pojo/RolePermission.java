package com.oneself.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @author liuhuan
 * date 2025/9/2
 * packageName com.oneself.model.pojo
 * className RolePermission
 * description 角色权限关联表
 * version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role_permission")
public class RolePermission extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private UUID id;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private UUID roleId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private UUID permId;
}
