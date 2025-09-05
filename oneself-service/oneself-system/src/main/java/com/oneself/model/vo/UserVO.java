package com.oneself.model.vo;

import com.oneself.annotation.Sensitive;
import com.oneself.model.enums.SexEnum;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.user.model.vo
 * className UserVO
 * description 用户信息
 * version 1.0
 */
@Data
@Schema(description = "用户信息 VO")
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
}
