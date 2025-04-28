package com.oneself.model.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2024/12/9
 * packageName com.oneself.common.model.pojo
 * className BasePojo
 * description 实体类基本参数
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasePojo implements Serializable {
    @Serial
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
