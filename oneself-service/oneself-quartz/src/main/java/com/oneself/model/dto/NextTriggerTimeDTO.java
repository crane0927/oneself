package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/31
 * packageName com.oneself.model.dto
 * className NextTriggerTimeDTO
 * description Cron 下次触发时间
 * version 1.0
 */
@Data
public class NextTriggerTimeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "CRON 表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "0 0 9/1 * * ? *")
    private String scheduleConf;
    @Schema(description = "后续执行时间数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer num;
}
