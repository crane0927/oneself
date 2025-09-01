package com.oneself.script.model.dto;

import com.oneself.script.model.bo.InputParamBO;
import com.oneself.script.model.bo.OutputParamBO;
import com.oneself.script.model.enums.LanguageEnum;
import com.oneself.script.model.enums.ScriptStatusEnum;
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

    @Schema(description = "脚本语言", allowableValues = {"PYTHON", "SHELL", "SQL", "DSL"})
    @NotBlank(message = "脚本语言不能为空")
    private LanguageEnum language;

    @Schema(description = "版本号")
    private String versionNum;

    @Schema(description = "是否为最新版本", defaultValue = "true")
    private Boolean latest = true;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private ScriptStatusEnum status = ScriptStatusEnum.DRAFT;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "脚本大小（字节）")
    private Long size;

    @Schema(description = "脚本内容校验和")
    private String checksum;

    @Schema(description = "输入参数列表")
    private List<InputParamBO> inputParams;

    @Schema(description = "输出参数列表")
    private List<OutputParamBO> outputParams;

//    @Schema(description = "创建人")
//    private String createBy;
//
//    @Schema(description = "创建时间")
//    private LocalDateTime createTime;
//
//    @Schema(description = "更新人")
//    private String updateBy;
//
//    @Schema(description = "更新时间")
//    private LocalDateTime updateTime;
}
