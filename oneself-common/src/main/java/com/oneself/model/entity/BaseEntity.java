package com.oneself.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2024/12/9
 * packageName com.oneself.common.model.entity
 * className BaseEntity
 * description 实体类基本参数
 * version 1.0
 */
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
