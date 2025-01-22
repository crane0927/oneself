package com.oneself.dept.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.common.model.enums.StatusEnum;
import com.oneself.model.pojo.BasePojo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.model.pojo
 * className Dept
 * description 部门信息实体类
 * version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Builder
@TableName("dept")
public class Dept extends BasePojo {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String deptName;
    private String deptDesc;
    private Integer sequence;
    private Long parentId;
    private String leader;
    private String phone;
    private String email;
    private StatusEnum status;
}
