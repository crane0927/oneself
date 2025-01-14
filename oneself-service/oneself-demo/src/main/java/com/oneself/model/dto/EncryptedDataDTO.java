package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/3
 * packageName com.oneself.model.dto
 * className EncryptedDataDTO
 * description 加密数据 DTO
 * version 1.0
 */
@Data
public class EncryptedDataDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "加密后的密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    private String encryptedSymmetricKey;
    @Schema(description = "加密后的数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] encryptedData;
}
