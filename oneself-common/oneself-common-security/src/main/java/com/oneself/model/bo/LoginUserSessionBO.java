package com.oneself.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.model.bo
 * className LoginUserSessionBO
 * description
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserSessionBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * sessionId，短唯一ID，作为Redis key
     */
    private String sessionId;

    /**
     * 登录时间
     */
    private String loginTime;
}
