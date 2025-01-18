package com.oneself.dept.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.model.dto
 * className AddDeptDTO
 * description 新增部门 DTO
 * version 1.0
 */
@Data
public class AddDeptDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deptName;
    @Schema(description = "部门描述")
    private String deptDesc;
    @Schema(description = "父节点 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;
    @Schema(description = "部门负责人", requiredMode = Schema.RequiredMode.REQUIRED)
    private String leader;
    @Schema(description = "负责人电话", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    @Schema(description = "负责人邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
