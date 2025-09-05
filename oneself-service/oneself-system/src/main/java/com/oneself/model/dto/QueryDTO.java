package com.oneself.model.dto;

import com.oneself.model.enums.StatusEnum;
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
@Schema(name = "QueryDTO", description = "分页查询部分信息 DTO")
public class QueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "状态")
    private StatusEnum status;
}
