package com.oneself.service;

import com.oneself.model.dto.LoginDTO;
import com.oneself.model.vo.CaptchaVO;
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

    /**
     * 登出
     *
     * @param request HTTP 请求
     * @return 是否登出成功
     */
    Boolean logout(HttpServletRequest request);

    /**
     * 刷新 Token
     *
     * @param request HTTP 请求
     * @return 新的 token
     */
    String refresh(HttpServletRequest request);

    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    CaptchaVO generateCaptcha();
}
