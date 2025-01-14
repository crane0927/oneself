package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className DataMapDTO
 * description 任务参数 DTO
 * version 1.0
 */
@Data
public class DataMapDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String key;
    @Schema(description = "值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Object value;
}
