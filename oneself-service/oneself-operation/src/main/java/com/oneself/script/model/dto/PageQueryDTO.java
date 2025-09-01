package com.oneself.script.model.dto;

import com.oneself.script.model.enums.ScriptStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/8/27
 * packageName com.oneself.script.model.dto
 * className PageQueryDTO
 * description 分页查询 DTO
 * version 1.0
 */
@Data
public class PageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "脚本名称，支持模糊查询")
    private String name;

    @Schema(description = "脚本状态")
    private ScriptStatusEnum status;

    @Schema(description = "标签列表，任意标签匹配")
    private List<String> tags;
}
