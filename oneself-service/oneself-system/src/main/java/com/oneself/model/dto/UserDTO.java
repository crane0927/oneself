package com.oneself.model.dto;

import com.oneself.annotation.Sensitive;
import com.oneself.model.enums.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.dto
 * className UserDTO
 * description 新增用户 DTO
 * version 1.0
 */
@Data
@Schema(name = "UserDTO", description = "新增用户 DTO")
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


}