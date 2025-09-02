package com.oneself.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.model.enums.StatusEnum;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.io.Serial;
import java.util.UUID;

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
@TableName("dept")
public class Dept extends BasePojo {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private UUID id;

    /**
     * 部门名称
     */
    @TableField("name")
    private String name;

    /**
     * 父部门ID
     */
    @TableField("parent_id")
    private UUID parentId;

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
