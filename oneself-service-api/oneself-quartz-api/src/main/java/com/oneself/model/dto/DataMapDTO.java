package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className DataMapDTO
 * description 任务参数 DTO
 * version 1.0
 */
@Data
public class DataMapDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "键", required = true)
    private String key;
    @ApiModelProperty(value = "值", required = true)
    private Object value;
}
