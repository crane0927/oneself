package com.oneself.script.model.bo;

import com.oneself.script.model.enums.ParamTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/8/28
 * packageName com.oneself.script.model.bo
 * className InputParamBO
 * description 输入参数 BO
 * version 1.0
 */
@Data
@Schema(description = "输入参数 BO")
public class InputParamBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "输入参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "输入参数名称不能为空")
    @Size(max = 50, message = "输入参数名称长度不能超过 50 个字符")
    private String name;

    @Schema(description = "输入参数描述")
    @Size(max = 200, message = "输入参数描述长度不能超过 200 个字符")
    private String description;

    @Schema(description = "输入参数类型", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"STRING", "NUMBER", "BOOLEAN", "DATE", "ENUM"})
    @NotNull(message = "输入参数类型不能为空")
    private ParamTypeEnum type;

    @Schema(description = "是否必填（true-必填，false-可选）", defaultValue = "false")
    private boolean required;

    @Schema(description = "默认值")
    @Size(max = 200, message = "默认值长度不能超过 200 个字符")
    private String defaultValue;

    @Schema(description = "实际传入的参数值")
    @Size(max = 200, message = "参数值长度不能超过 200 个字符")
    private String paramValue;

    @Schema(description = "是否允许编辑", defaultValue = "true")
    private boolean editable = true;

    @Schema(description = "是否多选", defaultValue = "false")
    private boolean multipleChoice;
}
