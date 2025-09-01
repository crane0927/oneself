package com.oneself.script.model.vo;

import com.oneself.script.model.bo.InputParamBO;
import com.oneself.script.model.bo.OutputParamBO;
import com.oneself.script.model.mongodb.Script;
import com.oneself.utils.BeanCopyUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/8/27
 * packageName com.oneself.script.model.vo
 * className ScriptVO
 * description
 * version 1.0
 */
@Data
@Schema(description = "脚本 VO，用于前端展示")
public class ScriptVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "脚本 ID")
    private String id;

    @Schema(description = "脚本名称")
    private String name;

    @Schema(description = "脚本描述")
    private String description;

    @Schema(description = "脚本内容")
    private String content;

    @Schema(description = "脚本语言")
    private String language;

    @Schema(description = "版本号")
    private String versionNum;

    @Schema(description = "是否为最新版本")
    private Boolean latest;

    @Schema(description = "状态：draft/active/deprecated")
    private String status;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "输入参数列表")
    private List<InputParamBO> inputParams;

    @Schema(description = "输出参数列表")
    private List<OutputParamBO> outputParams;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "历史版本列表（可选，用于前端展示版本切换）")
    private List<VersionInfoVO> versionHistory;

    @Data
    @Schema(description = "版本信息 VO")
    public static class VersionInfoVO implements Serializable {
        private String versionNum;
        private Boolean latest;
        private LocalDateTime updateTime;
    }

    public ScriptVO(Script val) {
        if (val == null) return;
        // 复制同名字段
        BeanCopyUtils.copy(val, this);
    }
}