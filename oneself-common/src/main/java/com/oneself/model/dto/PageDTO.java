package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.common.model.dto
 * className PageDTO
 * description 通用分页查询 DTO
 * version 1.0
 */
@Data
public class PageDTO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "查询条件", requiredMode = Schema.RequiredMode.REQUIRED)
    private T condition;
    @Schema(description = "分页信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private Pagination pagination;

    @Data
    public static class Pagination {
        @Schema(description = "页码", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long pageNum = 1L;
        @Schema(description = "页面大小", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long pageSize = 50L;
        @Schema(description = "排序字段")
        private String sort;
    }
}