package com.oneself.system.model.dto;

import com.oneself.system.model.enums.ConfigurationTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.model.dto
 * className ConfigurationQueryDTO
 * description
 * version 1.0
 */
@Data
@Schema(name = "ConfigurationQueryDTO", description = "系统配置查询参数对象")
public class ConfigurationQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置名称（支持模糊查询）")
    private String name;

    @Schema(description = "参数键（支持精确匹配）")
    private String paramKey;

    @Schema(description = "配置类型（0=系统参数，1=业务参数）")
    private ConfigurationTypeEnum type;
}
