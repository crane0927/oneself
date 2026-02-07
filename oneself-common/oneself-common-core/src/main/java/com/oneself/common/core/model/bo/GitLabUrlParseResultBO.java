package com.oneself.common.core.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/12
 * packageName com.oneself.model.bo
 * className GitLabUrlParseResultBO
 * description
 * version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GitLab URL 解析结果 BO")
public class GitLabUrlParseResultBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(name = "GitLab URL")
    private String gitlabUrl;
    @Schema(name = "项目路径")
    private String projectPath;

}