package com.oneself.model.vo;

import com.oneself.model.enums.ResourceTypeEnum;
import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.vo
 * className PermissionVO
 * description 
 * version 1.0
 */
@Data
@Schema(name = "PermissionVO", description = "权限信息数据传输对象")
public class PermissionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "权限ID")
    private String id;

    @Schema(description = "权限编码")
    private String permCode;

    @Schema(description = "权限名称")
    private String permName;

    @Schema(description = "权限描述")
    private String description;

    @Schema(description = "资源类型(menu-菜单, button-按钮, api-接口)")
    private ResourceTypeEnum resourceType;

    @Schema(description = "资源路径")
    private String resourcePath;

    @Schema(description = "父权限ID")
    private String parentId;

    @Schema(description = "排序")
    private Integer sortOrder;

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
