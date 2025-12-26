package com.oneself.model.dto;

import com.oneself.model.enums.ConstraintTypeEnum;
import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.model.dto
 * className ConstraintQueryDTO
 * description 约束配置查询条件对象
 * version 1.0
 */
@Data
@Schema(name = "ConstraintQueryDTO", description = "约束配置查询条件对象")
public class ConstraintQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "约束类型")
    private ConstraintTypeEnum constraintType;

    @Schema(description = "约束名称（模糊查询）")
    private String constraintName;

    @Schema(description = "状态")
    private StatusEnum status;
}

