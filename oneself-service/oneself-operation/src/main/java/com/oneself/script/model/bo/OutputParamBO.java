package com.oneself.script.model.bo;

import com.oneself.script.model.enums.ParamTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "输出参数 BO")
public class OutputParamBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "输出参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "输出参数名称不能为空")
    @Size(max = 50, message = "输出参数名称长度不能超过 50 个字符")
    private String name;

    @Schema(description = "输出参数描述")
    @Size(max = 200, message = "输出参数描述长度不能超过 200 个字符")
    private String description;

    @Schema(description = "输出参数类型", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"STRING", "NUMBER", "BOOLEAN", "DATE", "ENUM"})
    @NotNull(message = "输出参数类型不能为空")
    private ParamTypeEnum type;
}