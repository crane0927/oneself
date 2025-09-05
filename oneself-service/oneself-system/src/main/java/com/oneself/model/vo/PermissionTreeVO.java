package com.oneself.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.model.vo
 * className PermissionTreeVO
 * description 
 * version 1.0
 */
@Data
@Schema(name = "PermissionTreeVO", description = "权限树形数据传输对象")
public class PermissionTreeVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
