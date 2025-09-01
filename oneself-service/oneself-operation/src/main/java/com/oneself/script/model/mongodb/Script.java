package com.oneself.script.model.mongodb;

import com.oneself.script.model.bo.InputParamBO;
import com.oneself.script.model.bo.OutputParamBO;
import com.oneself.script.model.enums.ScriptStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.script.model.mongodb
 * className Script
 * description
 * version 1.0
 */
@Data
@Document(collection = "scripts")
public class Script implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键（UUID 或 ObjectId 的字符串形式）
     */
    @Id
    private String id;

    /**
     * 脚本名称
     */
    private String name;

    /**
     * 脚本描述
     */
    private String description;

    /**
     * 脚本内容（代码本体）
     */
    private String content;

    /**
     * 脚本语言（Python/Shell/SQL/DSL 等）
     */
    private String language;

    /**
     * 版本号（如 v1.0.0）
     */
    private String versionNum;

    /**
     * 是否最新版本
     */
    private Boolean latest = true;

    /**
     * 状态：draft / active / deprecated
     */
    private ScriptStatusEnum status;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 脚本大小（字节数）
     */
    private Long size;

    /**
     * 内容校验和（MD5/SHA256）
     */
    private String checksum;

    /**
     * 输入参数列表
     */
    private List<InputParamBO> inputParams;

    /**
     * 输出参数列表
     */
    private List<OutputParamBO> outputParams;

    /**
     * 逻辑删除标记
     */
    private Boolean deleted = false;

    /**
     * 创建人
     */
    @CreatedBy
    private String createBy;

    /**
     * 创建时间
     */
    @CreatedDate
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @LastModifiedBy
    private String updateBy;

    /**
     * 更新时间
     */
    @LastModifiedDate
    private LocalDateTime updateTime;
}
