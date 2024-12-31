package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/31
 * packageName com.oneself.model.dto
 * className NextTriggerTimeDTO
 * description Cron 下次触发时间
 * version 1.0
 */
@Data
public class NextTriggerTimeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "CRON 表达式", required = true,example = "0 0 9/1 * * ? *")
    private String scheduleConf;
    @ApiModelProperty(value = "后续执行时间数量", required = true,example = "1")
    private Integer num;
}
