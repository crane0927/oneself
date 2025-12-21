package com.oneself.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.model.vo
 * className CaptchaVO
 * description 验证码响应对象
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码响应对象")
public class CaptchaVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "验证码ID", example = "abc123def456")
    private String captchaId;

    @Schema(description = "验证码图片（Base64编码）", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...")
    private String captchaImage;
}

