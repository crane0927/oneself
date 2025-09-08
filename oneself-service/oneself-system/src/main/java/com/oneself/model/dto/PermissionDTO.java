package com.oneself.model.dto;

import com.oneself.model.enums.ResourceTypeEnum;
import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.dto
 * className PermissionDTO
 * description 
 * version 1.0
 */
@Data
@Schema(name = "PermissionDTO", description = "权限数据传输对象")
public class PermissionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "权限编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "权限编码不能为空")
    private String permCode;

    @Schema(description = "权限名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "权限名称不能为空")
    private String permName;

    @Schema(description = "权限描述")
    private String description;

    @Schema(description = "资源类型(menu-菜单, button-按钮, api-接口)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "资源类型不能为空")
    private ResourceTypeEnum resourceType;

    @Schema(description = "资源路径")
    private String resourcePath;

    @Schema(description = "父权限ID")
    private String parentId;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private StatusEnum status;
}