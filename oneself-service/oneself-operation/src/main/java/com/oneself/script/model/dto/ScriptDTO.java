package com.oneself.script.model.dto;

import com.oneself.script.model.bo.InputParamBO;
import com.oneself.script.model.bo.OutputParamBO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/8/27
 * packageName com.oneself.script.model.dto
 * className ScriptDTO
 * description 脚本 DTO
 * version 1.0
 */
@Data
@Schema(description = "脚本 DTO")
public class ScriptDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "脚本名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "脚本名称不能为空")
    @Size(max = 50, message = "脚本名称长度不能超过 50 个字符")
    private String name;

    @Schema(description = "脚本描述")
    @Size(max = 200, message = "脚本描述长度不能超过 200 个字符")
    private String description;

    @Schema(description = "脚本内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "脚本内容不能为空")
    private String content;

    @Schema(description = "输入参数列表")
    private List<InputParamBO> inputParams;

    @Schema(description = "输出参数列表")
    private List<OutputParamBO> outputParams;
}
