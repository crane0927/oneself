package com.oneself.dept.model.dto;

import com.oneself.common.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.model.dto
 * className PageDeptDTO
 * description 分页查询部分信息 DTO
 * version 1.0
 */
@Data
public class PageDeptDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "部门负责人")
    private String leader;
    @Schema(description = "部门描述")
    private String deptDesc;
    @Schema(description = "部门状态")
    private StatusEnum status;
}
