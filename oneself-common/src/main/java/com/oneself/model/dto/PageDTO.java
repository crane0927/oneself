package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "查询条件")
    private T condition;
    @ApiModelProperty(value = "分页信息")
    private Pagination pagination;

    @Data
    public static class Pagination {
        @ApiModelProperty(value = "页码", example = "1")
        private Long pageNum = 1L;
        @ApiModelProperty(value = "页面大小", example = "50")
        private Long pageSize = 50L;
        @ApiModelProperty(value = "排序字段", example = "createTime")
        private String sort;
    }
}