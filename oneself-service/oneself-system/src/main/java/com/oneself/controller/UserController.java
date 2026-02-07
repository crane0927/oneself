package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.dto.UserQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.UserVO;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import com.oneself.resp.Resp;
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
    public Resp<String> add(@RequestBody @Valid UserDTO dto) {
        return Resp.success(userService.add(dto));
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Resp<UserVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return Resp.success(userService.get(id));
    }

    /** 原则 6 合规：名词化路径。保留旧路径兼容，计划移除。 */
    @Operation(summary = "根据用户名查询用户（RESTful）")
    @GetMapping("/by-name/{name}")
    public Resp<UserVO> getUserByUsername(@PathVariable @Valid @NotBlank String name) {
        return Resp.success(userService.getUserByName(name));
    }

    @Deprecated
    @Operation(summary = "根据用户名查询会话信息（已废弃，请使用 GET /user/by-name/{name}）")
    @GetMapping("/get/user/by/{name}")
    public Resp<UserVO> getUserByName(@PathVariable @Valid @NotBlank String name) {
        return Resp.success(userService.getUserByName(name));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public Resp<Boolean> update(@PathVariable @Valid @NotBlank String id,
                                @RequestBody @Valid UserDTO dto) {
        return Resp.success(userService.update(id, dto));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return Resp.success(userService.delete(ids));
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/status/{status}")
    public Resp<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids,
                                      @PathVariable @Valid @NotBlank StatusEnum status) {
        return Resp.success(userService.updateStatus(ids, status));
    }

    @Operation(summary = "分页查询用户")
    @PostMapping("/page")
    public PageResp<UserVO> page(@RequestBody @Valid PageReq<UserQueryDTO> dto) {
        return userService.page(dto);
    }

    /** 原则 6 合规：名词化路径。保留旧路径兼容，计划移除。 */
    @Operation(summary = "根据部门 ID 查询用户")
    @GetMapping("/by-dept/{deptId}")
    public Resp<List<UserVO>> listByDept(@PathVariable @Valid @NotBlank String deptId) {
        return Resp.success(userService.listByDeptId(deptId));
    }

    @Deprecated
    @Operation(summary = "根据部门 ID 查询用户（已废弃，请使用 GET /user/by-dept/{deptId}）")
    @GetMapping("/list/by/dept/{deptId}")
    public Resp<List<UserVO>> listByDeptId(@PathVariable @Valid @NotBlank String deptId) {
        return Resp.success(userService.listByDeptId(deptId));
    }
}
