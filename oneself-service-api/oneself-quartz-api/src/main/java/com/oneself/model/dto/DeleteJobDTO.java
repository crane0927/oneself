package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className DeleteJobDTO
 * description 删除任务 DTO
 * version 1.0
 */
@Data
public class DeleteJobDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "任务名称", required = true)
    private String JobName;
    @ApiModelProperty(value = "任务组名称", required = true)
    private String JobGroupName;
    @ApiModelProperty(value = "触发器组名称", required = true)
    private String triggerGroupName;
    @ApiModelProperty(value = "触发器前缀", required = true)
    private String triggerPrefix;
}
