package com.oneself.service;

import com.oneself.model.vo.CaptchaVO;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.service
 * interfaceName CaptchaService
 * description 验证码服务接口
 * version 1.0
 */
public interface CaptchaService {
    /**
     * 生成验证码
     *
     * @return 验证码信息（包含验证码ID和Base64图片）
     */
    CaptchaVO generateCaptcha();

    /**
     * 验证验证码
     *
     * @param captchaId   验证码ID
     * @param captchaCode 用户输入的验证码
     * @return 验证是否通过
     */
    boolean validateCaptcha(String captchaId, String captchaCode);
}

