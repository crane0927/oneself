package com.oneself.model.dto;

import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.dto
 * className RoleQueryDTO
 * description
 * version 1.0
 */
@Data
@Schema(name = "RoleQueryDTO", description = "角色查询条件对象")
public class RoleQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色编码（模糊查询）")
    private String roleCode;

    @Schema(description = "角色名称（模糊查询）")
    private String roleName;

    @Schema(description = "状态")
    private StatusEnum status;
}
