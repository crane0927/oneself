package com.oneself.system.model.vo;

import com.oneself.common.feature.security.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.vo
 * className RoleVO
 * description
 * version 1.0
 */
@Data
@Schema(name = "RoleVO", description = "角色信息数据传输对象")
public class RoleVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    private String id;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "父角色ID（用于角色继承，RBAC1）")
    private String parentId;

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
