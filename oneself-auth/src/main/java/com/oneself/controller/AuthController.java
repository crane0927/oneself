package com.oneself.controller;

import com.oneself.model.dto.LoginDTO;
import com.oneself.model.vo.CaptchaVO;
import com.oneself.resp.Resp;
import com.oneself.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.controller
 * className LoginController
 * description AuthController
 * version 1.0
 */
@Tag(name = "身份认证")
@Slf4j
@RequiredArgsConstructor
@RestController
@RefreshScope
@RequestMapping({"/auth"})
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Resp<String> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String ip = getClientIp(request);
        log.info("收到登录请求，username={}, ip={}, userAgent={}",
                dto.getUsername(), ip, request.getHeader("User-Agent"));
        try {
            String token = authService.login(dto, request);
            long duration = System.currentTimeMillis() - startTime;
            log.info("登录请求处理完成，username={}, ip={}, duration={}ms",
                    dto.getUsername(), ip, duration);
            return Resp.success(token);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("登录请求处理失败，username={}, ip={}, duration={}ms, error={}",
                    dto.getUsername(), ip, duration, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "登出")
    @DeleteMapping("/logout")
    public Resp<Boolean> logout(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String ip = getClientIp(request);
        log.info("收到登出请求，ip={}", ip);
        try {
            Boolean result = authService.logout(request);
            long duration = System.currentTimeMillis() - startTime;
            log.info("登出请求处理完成，ip={}, duration={}ms", ip, duration);
            return Resp.success(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("登出请求处理失败，ip={}, duration={}ms, error={}", ip, duration, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public Resp<String> refresh(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        String ip = getClientIp(request);
        log.info("收到Token刷新请求，ip={}", ip);
        try {
            String token = authService.refresh(request);
            long duration = System.currentTimeMillis() - startTime;
            log.info("Token刷新请求处理完成，ip={}, duration={}ms", ip, duration);
            return Resp.success(token);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("Token刷新请求处理失败，ip={}, duration={}ms, error={}", ip, duration, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Resp<CaptchaVO> getCaptcha() {
        long startTime = System.currentTimeMillis();
        log.debug("收到验证码生成请求");
        try {
            CaptchaVO captcha = authService.generateCaptcha();
            long duration = System.currentTimeMillis() - startTime;
            log.debug("验证码生成完成，captchaId={}, duration={}ms", captcha.getCaptchaId(), duration);
            return Resp.success(captcha);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("验证码生成失败，duration={}ms", duration, e);
            throw e;
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况（取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
