package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

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
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String JobName;
    @Schema(description = "任务类全限定名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobClassName;
    @Schema(description = "任务组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String JobGroupName;
    @Schema(description = "触发器组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String triggerGroupName;
    @Schema(description = "触发器前缀", requiredMode = Schema.RequiredMode.REQUIRED)
    private String triggerPrefix;
    @Schema(description = "cron 表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "*/5 * * * * ?")
    private String cronExpression;
    @Schema(description = "任务参数")
    private Map<String, Object> dataMap;

}
