package com.oneself.system.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.common.infra.jdbc.model.pojo.BasePojo;
import lombok.*;

import java.io.Serial;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.model.pojo
 * className Dept
 * description 部门表
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class Dept extends BasePojo {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 父部门ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}
