package com.oneself.user.controller;

import com.oneself.annotation.LogRequestDetails;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.vo.ResponseVO;
import com.oneself.user.model.dto.AddUserDTO;
import com.oneself.user.model.vo.UserVO;
import com.oneself.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@LogRequestDetails
@RequireLogin
@RestController
@RequestMapping({"/user"})
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "新增用户")
    @PostMapping({"/add"})
    public ResponseVO<Boolean> add(@RequestBody AddUserDTO dto) {
        Integer size = userService.addUser(dto);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("新增用户失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "根据 ID 查询用户")
    @GetMapping({"/get/{id}"})
    public ResponseVO<UserVO> get(@PathVariable("id") Long id) {
        return ResponseVO.success(userService.getUser(id));
    }
}
