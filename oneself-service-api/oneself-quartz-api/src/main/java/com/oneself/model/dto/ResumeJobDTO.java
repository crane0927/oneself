package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className ResumeJobDTO
 * description 恢复任务 DTO
 * version 1.0
 */
@Data
public class ResumeJobDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "任务组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String JobGroupName;
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String JobName;
}