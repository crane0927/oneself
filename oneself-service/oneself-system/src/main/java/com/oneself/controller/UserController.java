package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.dto.UserQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.UserSessionVO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.model.vo.UserVO;
import com.oneself.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@ApiLog
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "新增用户")
    @PostMapping
    public ResponseVO<String> add(@RequestBody @Valid UserDTO dto) {
        return ResponseVO.success(userService.add(dto));
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public ResponseVO<UserVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return ResponseVO.success(userService.get(id));
    }

    @Operation(summary = "根据用户名查询会话信息")
    @GetMapping("/get/session/by/{name}")
    public ResponseVO<UserSessionVO> getSessionByName(@PathVariable("name") @Valid @NotBlank String name) {
        return ResponseVO.success(userService.getSessionByName(name));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public ResponseVO<Boolean> update(@PathVariable("id") @Valid @NotBlank String id,
                                      @RequestBody @Valid UserDTO dto) {
        return ResponseVO.success(userService.update(id, dto));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping
    public ResponseVO<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return ResponseVO.success(userService.delete(ids));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/status/{status}")
    public ResponseVO<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids,
                                            @PathVariable("status") @Valid @NotBlank StatusEnum status) {
        return ResponseVO.success(userService.updateStatus(ids, status));
    }

    @Operation(summary = "分页查询用户")
    @PostMapping("/page")
    public PageVO<UserVO> page(@RequestBody @Valid PageDTO<UserQueryDTO> dto) {
        return userService.page(dto);
    }

    @Operation(summary = "根据部门ID查询用户")
    @GetMapping("/list/by/dept/{deptId}")
    public ResponseVO<List<UserVO>> listByDeptId(@PathVariable("deptId") @Valid @NotBlank String deptId) {
        return ResponseVO.success(userService.listByDeptId(deptId));
    }
}
