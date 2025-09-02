package com.oneself.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.model.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.controller
 * className LoginController
 * description 登录认证
 * version 1.0
 */
@Tag(name = "登录认证")
@Slf4j
@RequiredArgsConstructor
@RestController
@RefreshScope
@RequestMapping({"/login"})
public class LoginController {

    @RequestLogging
    @Operation(summary = "你好 xxx")
    @GetMapping("/hello")
    public ResponseVO<String> sayHello() {
        return ResponseVO.success("hello");
    }
}
