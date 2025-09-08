package com.oneself.model.dto;

import com.oneself.model.enums.ResourceTypeEnum;
import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.dto
 * className PermissionQueryDTO
 * description 
 * version 1.0
 */
@Data
@Schema(name = "PermissionQueryDTO", description = "权限查询条件对象")
public class PermissionQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "权限编码（模糊查询）")
    private String permCode;

    @Schema(description = "权限名称（模糊查询）")
    private String permName;

    @Schema(description = "资源类型")
    private ResourceTypeEnum resourceType;

    @Schema(description = "状态")
    private StatusEnum status;
}
