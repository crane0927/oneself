package com.oneself.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.dto
 * className PermissionDTO
 * description 
 * version 1.0
 */
@Data
@Schema(name = "PermissionDTO", description = "权限数据传输对象")
public class PermissionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
