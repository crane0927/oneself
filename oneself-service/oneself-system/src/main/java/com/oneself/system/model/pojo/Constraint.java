package com.oneself.system.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.common.feature.security.model.enums.ConstraintTypeEnum;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.common.infra.jdbc.model.pojo.BasePojo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.model.pojo
 * className Constraint
 * description 约束配置表（RBAC2）
 * version 1.0
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("sys_constraint")
public class Constraint extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 约束类型(ROLE_MUTEX-角色互斥,PERM_MUTEX-权限互斥,CARDINALITY-基数约束,PREREQUISITE-先决条件)
     */
    @TableField("constraint_type")
    private ConstraintTypeEnum constraintType;

    /**
     * 约束名称
     */
    @TableField("constraint_name")
    private String constraintName;

    /**
     * 约束值(JSON格式，存储具体的约束规则)
     */
    @TableField("constraint_value")
    private String constraintValue;

    /**
     * 约束描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}

