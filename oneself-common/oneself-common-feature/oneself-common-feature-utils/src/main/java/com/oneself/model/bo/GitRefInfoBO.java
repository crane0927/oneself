package com.oneself.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.model.bo
 * className GitRefInfoBO
 * description
 * version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Git 引用信息 BO")
public class GitRefInfoBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "名称")
    private String name;
    @Schema(name = "最后提交")
    private String lastCommit;
    @Schema(name = "最后提交短 ID")
    private String shortId;
}
