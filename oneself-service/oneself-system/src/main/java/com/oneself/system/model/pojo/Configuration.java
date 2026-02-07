package com.oneself.system.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.system.model.enums.ConfigurationTypeEnum;
import com.oneself.common.infra.jdbc.model.pojo.BasePojo;
import lombok.*;

import java.io.Serial;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.model.pojo
 * className Configuration
 * description
 * version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_configuration")
public class Configuration extends BasePojo {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("name")
    private String name;

    @TableField("param_key")
    private String paramKey;

    @TableField("param_value")
    private String paramValue;

    @TableField("type")
    private ConfigurationTypeEnum type;

    @TableField("remark")
    private String remark;
}
