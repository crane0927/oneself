package com.oneself.service;

import com.oneself.model.dto.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author liuhuan
 * date 2025/9/11
 * packageName com.oneself.service
 * interfaceName AuthService
 * description
 * version 1.0
 */
public interface AuthService {
    /**
     * 登录
     *
     * @param dto 登录信息
     * @return 登录成功后的 token
     */
    String login(LoginDTO dto, HttpServletRequest request);

    Boolean logout(HttpServletRequest request);
}
