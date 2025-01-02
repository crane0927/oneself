package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className CronJobDTO
 * description 新增定时任务 DTO
 * version 1.0
 */
@Data
public class CronJobDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "任务名称", required = true)
    private String JobName;
    @ApiModelProperty(value = "任务类全限定名称", required = true)
    private String jobClassName;
    @ApiModelProperty(value = "任务组名称", required = true)
    private String JobGroupName;
    @ApiModelProperty(value = "触发器组名称", required = true)
    private String triggerGroupName;
    @ApiModelProperty(value = "触发器前缀", required = true)
    private String triggerPrefix;
    @ApiModelProperty(value = "cron 表达式", required = true, example = "*/5 * * * * ?")
    private String cronExpression;
    @ApiModelProperty(value = "任务参数")
    private List<DataMapDTO> dataMapList;

}
