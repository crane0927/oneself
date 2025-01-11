package com.oneself.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/4
 * packageName com.oneself.model.vo
 * className QuartzTaskVO
 * description
 * version 1.0
 */
@Data
public class QuartzTaskVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务名称")
    private String jobName;
    @ApiModelProperty(value = "任务组名称")
    private String jobGroup;
    @ApiModelProperty(value = "任务描述")
    private String description;
    @ApiModelProperty(value = "任务类全限定名")
    private String jobClassName;
    @ApiModelProperty(value = "触发器名称")
    private String triggerName;
    @ApiModelProperty(value = "触发器组名称")
    private String triggerGroup;
    @ApiModelProperty(value = "触发器状态（如 WAITING、PAUSED、ACQUIRED 等）")
    private String triggerState;
    @ApiModelProperty(value = "Cron 表达式（仅当 Cron Trigger 时有值）")
    private String cronExpression;
    @ApiModelProperty(value = "重复次数（仅当 Simple Trigger 时有值）")
    private Integer repeatCount;
    @ApiModelProperty(value = " 重复间隔（毫秒，Simple Trigger 时有值）")
    private Long repeatInterval;

}
