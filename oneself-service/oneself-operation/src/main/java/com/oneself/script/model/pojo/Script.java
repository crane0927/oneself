package com.oneself.script.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.model.pojo.BasePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuhuan
 * date 2025/8/28
 * packageName com.oneself.script.model.pojo
 * className Script
 * description 脚本信息表
 * version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("script")
public class Script extends BasePojo {
    private Long id;
    private String name;
    private String description;
    private String content;

}
