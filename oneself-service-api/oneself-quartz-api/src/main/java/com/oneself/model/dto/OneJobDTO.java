package com.oneself.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className OneJobDTO
 * description 创建仅执行一次的任务 DTO
 * version 1.0
 */
@Data
public class OneJobDTO implements Serializable {
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @Schema(description = "任务计划时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-12-30 15:30")
    private LocalDateTime executionTime;
    @Schema(description = "任务参数")
    private Map<String, Object> dataMap;
}

