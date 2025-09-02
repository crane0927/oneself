package com.oneself.model.pojo;

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
 * className UserRole
 * description 用户角色关联表
 * version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_role")
public class UserRole extends BasePojo {

    @TableId
    private UUID id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private UUID userId;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private UUID roleId;
}