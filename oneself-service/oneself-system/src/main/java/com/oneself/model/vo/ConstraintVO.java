package com.oneself.model.vo;

import com.oneself.model.enums.ConstraintTypeEnum;
import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.model.vo
 * className ConstraintVO
 * description 约束配置信息数据传输对象（RBAC2）
 * version 1.0
 */
@Data
@Schema(name = "ConstraintVO", description = "约束配置信息数据传输对象")
public class ConstraintVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "约束ID")
    private String id;

    @Schema(description = "约束类型")
    private ConstraintTypeEnum constraintType;

    @Schema(description = "约束名称")
    private String constraintName;

    @Schema(description = "约束值(JSON格式)")
    private String constraintValue;

    @Schema(description = "约束描述")
    private String description;

    @Schema(description = "状态")
    private StatusEnum status;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改人")
    private String updateBy;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}

