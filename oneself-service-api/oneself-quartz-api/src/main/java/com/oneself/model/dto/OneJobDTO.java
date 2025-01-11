package com.oneself.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @ApiModelProperty(value = "任务计划时间", required = true, example = "2024-12-30 15:30")
    private LocalDateTime executionTime;
    @ApiModelProperty(value = "任务参数")
    private Map<String, Object> dataMap;
}

