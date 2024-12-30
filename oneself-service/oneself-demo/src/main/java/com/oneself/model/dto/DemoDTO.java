package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className DemoDTO
 * description demo dto
 * version 1.0
 */
@Data
public class DemoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "姓名", required = true)
    private String name;
}
