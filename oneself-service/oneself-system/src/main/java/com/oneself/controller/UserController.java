package com.oneself.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.model.vo.UserVO;
import com.oneself.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "新增用户")
    @PostMapping
    public ResponseVO<String> add(@RequestBody @Valid UserDTO dto) {

        return ResponseVO.success(userService.add(dto));
    }

    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/{id}")
    public ResponseVO<UserVO> get(@PathVariable("id") @Valid @NotNull @Positive String id) {
        return ResponseVO.success(userService.get(id));
    }
}
