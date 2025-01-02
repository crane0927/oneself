package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className PauseJobDTO
 * description 暂停任务 DTO
 * version 1.0
 */
@Data
public class PauseJobDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "任务组名称", required = true)
    private String JobGroupName;
    @ApiModelProperty(value = "任务名称", required = true)
    private String JobName;
}
