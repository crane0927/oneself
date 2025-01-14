package com.oneself.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务组名称")
    private String jobGroup;
    @Schema(description = "任务描述")
    private String description;
    @Schema(description = "任务类全限定名")
    private String jobClassName;
    @Schema(description = "触发器名称")
    private String triggerName;
    @Schema(description = "触发器组名称")
    private String triggerGroup;
    @Schema(description = "触发器状态（如 WAITING、PAUSED、ACQUIRED 等）")
    private String triggerState;
    @Schema(description = "Cron 表达式（仅当 Cron Trigger 时有值）")
    private String cronExpression;
    @Schema(description = "重复次数（仅当 Simple Trigger 时有值）")
    private Integer repeatCount;
    @Schema(description = " 重复间隔（毫秒，Simple Trigger 时有值）")
    private Long repeatInterval;

}
