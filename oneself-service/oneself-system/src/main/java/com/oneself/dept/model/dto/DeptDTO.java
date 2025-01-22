package com.oneself.dept.model.dto;

import com.oneself.common.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DeptDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "部门描述")
    private String deptDesc;

    @Schema(description = "部门状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部门状态不能为空")
    private StatusEnum status;

    @Schema(description = "父节点 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "父节点 ID 不能为空")
    @PositiveOrZero(message = "父节点 ID 必须大于等于 0")
    private Long parentId;

    @Schema(description = "部门负责人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "部门负责人不能为空")
    private String leader;

    @Schema(description = "负责人电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "负责人电话不能为空")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "负责人电话格式不正确")
    private String phone;

    @Schema(description = "负责人邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "负责人邮箱不能为空")
    @Email(message = "负责人邮箱格式不正确")
    private String email;
}