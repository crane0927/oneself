package com.oneself.model.dto;

import com.oneself.annotation.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className DemoDTO
 * description demo dto
 * version 1.0
 */
@Data
public class DemoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Sensitive
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "姓名不能为空")
    private String name;
}
