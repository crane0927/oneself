package com.oneself.system.model.vo;

import com.oneself.system.model.enums.ConfigurationTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.model.dto
 * className ConfigurationVO
 * description
 * version 1.0
 */
@Data
@Schema(name = "ConfigurationVO", description = "系统配置信息视图对象")
public class ConfigurationVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置 ID")
    private String id;

    @Schema(description = "配置名称")
    private String name;

    @Schema(description = "参数键")
    private String paramKey;

    @Schema(description = "参数值")
    private String paramValue;

    @Schema(description = "配置类型（0=系统参数，1=业务参数）")
    private ConfigurationTypeEnum type;

    @Schema(description = "备注信息")
    private String remark;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改人")
    private String updateBy;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
