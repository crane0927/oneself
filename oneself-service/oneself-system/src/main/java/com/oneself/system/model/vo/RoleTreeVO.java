package com.oneself.system.model.vo;

import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.system.model.pojo.Role;
import com.oneself.common.core.utils.BeanCopyUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.model.vo
 * className RoleTreeVO
 * description 角色信息树结果 VO（支持角色继承，RBAC1）
 * version 1.0
 */
@Data
@Schema(name = "RoleTreeVO", description = "角色信息树结果 VO")
public class RoleTreeVO implements Serializable {
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

    @Schema(description = "子角色列表")
    private List<RoleTreeVO> children;

    public RoleTreeVO() {
        this.children = new ArrayList<>();
    }

    public RoleTreeVO(Role role) {
        BeanCopyUtils.copy(role, this);
        this.children = new ArrayList<>();
    }
}

