package com.oneself.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.req
 * className PageReq
 * description 通用分页查询 DTO
 * version 1.0
 */
@Data
@Schema(description = "分页查询参数")
public class PageReq<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "查询条件", requiredMode = Schema.RequiredMode.REQUIRED)
    private transient T condition;
    @Schema(description = "分页信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private Pagination pagination;

    @Data
    public static class Pagination implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        @Schema(description = "页码", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long pageNum = 1L;
        @Schema(description = "页面大小", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long pageSize = 50L;
        @Schema(description = "排序字段列表")
        private List<Sort> sorts;
    }

    @Data
    public static class Sort implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        @Schema(description = "字段名", example = "createTime")
        private String field;
        @Schema(description = "排序方向", example = "desc")
        private SortDirection direction = SortDirection.DESC;
    }

    @Getter
    public enum SortDirection {
        ASC, DESC
    }
}

