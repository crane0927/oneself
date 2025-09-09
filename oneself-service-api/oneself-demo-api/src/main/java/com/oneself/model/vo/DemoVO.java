package com.oneself.model.vo;

import com.oneself.annotation.Sensitive;
import com.oneself.model.enums.DesensitizedTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.vo
 * className DemoVO
 * description
 * version 1.0
 */
@Data
public class DemoVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Sensitive(DesensitizedTypeEnum.CHINESE_NAME)
    @Schema(description = "中文姓名")
    private String info;

}
