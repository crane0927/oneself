package com.oneself.dept.model.vo;

import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.model.pojo.Dept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

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
    private Long id;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "部门描述")
    private String deptDesc;
    @Schema(description = "父节点 ID")
    private Long parentId;
    @Schema(description = "负责人")
    private String leader;
    @Schema(description = "负责人手机号码")
    private String phone;
    @Schema(description = "负责人邮箱")
    private String email;
    private StatusEnum status;
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
        BeanUtils.copyProperties(val, this);
        this.children = new ArrayList<>();
    }
}
