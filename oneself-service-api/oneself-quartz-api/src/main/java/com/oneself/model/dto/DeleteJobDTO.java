package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String JobName;
    @Schema(description = "任务组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String JobGroupName;
    @Schema(description = "触发器组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String triggerGroupName;
    @Schema(description = "触发器前缀", requiredMode = Schema.RequiredMode.REQUIRED)
    private String triggerPrefix;
}
