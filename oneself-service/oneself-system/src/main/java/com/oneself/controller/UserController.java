package com.oneself.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.controller
 * className UserController
 * description 用户信息控制器
 * version 1.0
 */
@Tag(name = "用户信息")
@Slf4j
@RequiredArgsConstructor
@RequestLogging
@RequireLogin
@RestController
@RequestMapping({"/user"})
public class UserController {

    private final UserService userService;

}
