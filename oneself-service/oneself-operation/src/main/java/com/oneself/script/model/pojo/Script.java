package com.oneself.script.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/8/28
 * packageName com.oneself.script.model.pojo
 * className Script
 * description 脚本信息表
 * version 1.0
 */
@Data
@TableName("script")
public class Script implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String description;
    private String content;

}
