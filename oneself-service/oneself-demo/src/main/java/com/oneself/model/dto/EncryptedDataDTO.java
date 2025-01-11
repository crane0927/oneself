package com.oneself.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "加密后的密钥", required = true)
    private String encryptedSymmetricKey;
    @ApiModelProperty(value = "加密后的数据", required = true)
    private byte[] encryptedData;
}
