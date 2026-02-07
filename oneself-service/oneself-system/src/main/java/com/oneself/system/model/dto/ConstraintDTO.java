package com.oneself.system.model.dto;

import com.oneself.common.feature.security.model.enums.ConstraintTypeEnum;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.model.dto
 * className ConstraintDTO
 * description 约束配置数据传输对象（RBAC2）
 * version 1.0
 */
@Data
@Schema(name = "ConstraintDTO", description = "约束配置数据传输对象")
public class ConstraintDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "约束类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "约束类型不能为空")
    private ConstraintTypeEnum constraintType;

    @Schema(description = "约束名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "约束名称不能为空")
    private String constraintName;

    @Schema(description = "约束值(JSON格式)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "约束值不能为空")
    private String constraintValue;

    @Schema(description = "约束描述")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private StatusEnum status;
}

