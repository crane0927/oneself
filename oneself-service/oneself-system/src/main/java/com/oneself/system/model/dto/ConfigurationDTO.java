package com.oneself.system.model.dto;

import com.oneself.system.model.enums.ConfigurationTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.model.dto
 * className ConfigurationDTO
 * description
 * version 1.0
 */
@Data
@Schema(name = "ConfigurationDTO", description = "系统配置数据传输对象")
public class ConfigurationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置名称不能为空")
    private String name;

    @Schema(description = "参数键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数键不能为空")
    private String paramKey;

    @Schema(description = "参数值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数值不能为空")
    private String paramValue;

    @Schema(description = "配置类型（0=系统参数，1=业务参数）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "配置类型不能为空")
    private ConfigurationTypeEnum type;

    @Schema(description = "备注信息")
    private String remark;
}
