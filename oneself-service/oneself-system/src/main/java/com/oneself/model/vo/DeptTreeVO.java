package com.oneself.model.vo;

import com.oneself.model.enums.StatusEnum;
import com.oneself.model.pojo.Dept;
import com.oneself.utils.BeanCopyUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/1/20
 * packageName com.oneself.dept.model.vo
 * className DeptTreeVO
 * description 部门信息树结果 VO
 * version 1.0
 */
@Data
public class DeptTreeVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "部门 ID")
    private String id;
    @Schema(description = "部门名称")
    private String name;
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
    @Schema(description = "子节点")
    private List<DeptTreeVO> children;

    public DeptTreeVO() {
    }

    public DeptTreeVO(Dept val) {
        BeanCopyUtils.copy(val, this);
        this.children = new ArrayList<>();
    }
}
