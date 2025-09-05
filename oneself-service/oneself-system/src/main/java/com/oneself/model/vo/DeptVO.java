package com.oneself.model.vo;

import com.oneself.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.model.vo
 * className DeptVO
 * description 部门信息
 * version 1.0
 */
@Data
@Schema(description = "部门信息 VO")
public class DeptVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "部门 ID")
    private String id;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "父节点 ID")
    private String parentId;
    @Schema(description = "部门状态")
    private StatusEnum status;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "修改人")
    private String updateBy;
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
