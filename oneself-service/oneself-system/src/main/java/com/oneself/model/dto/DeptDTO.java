package com.oneself.model.dto;

import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "DeptDTO", description = "部门数据传输对象")
public class DeptDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "部门名称不能为空")
    private String name;

    @Schema(description = "父节点 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String parentId;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    @Schema(description = "部门状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部门状态不能为空")
    private StatusEnum status;
}