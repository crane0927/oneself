package com.oneself.controller;

import com.oneself.model.dto.LoginDTO;
import com.oneself.model.vo.ResponseVO;
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

    @Operation(summary = "登录")
    @PostMapping("/login")
    public ResponseVO<String> login(@RequestBody LoginDTO dto) {
        // 1. 验证用户名密码
        // 2. 生成 Token，存入 Redis
        // 3. 返回 Token
        return ResponseVO.success(null);
    }

    @Operation(summary = "登出")
    @DeleteMapping("/logout")
    public ResponseVO<Boolean> logout(HttpServletRequest request) {
        return ResponseVO.success(null);
    }

    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public ResponseVO<Boolean> refresh(HttpServletRequest request) {
        return ResponseVO.success(true);
    }
}
