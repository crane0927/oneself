package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.vo.RoleVO;
import com.oneself.resp.Resp;
import com.oneself.service.UserRoleService;
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
 * date 2025/9/5
 * packageName com.oneself.controller
 * className UserRoleController
 * description
 * version 1.0
 */
@Tag(name = "用户角色关联")
@Slf4j
@RequiredArgsConstructor
@ApiLog
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(summary = "给用户分配角色")
    @PostMapping("/assign")
    public Resp<Boolean> assignRoles(@RequestParam @Valid @NotBlank String userId,
                                     @RequestBody @Valid @NotEmpty List<@NotBlank String> roleIds) {
        return Resp.success(userRoleService.assignRoles(userId, roleIds));
    }

    @Operation(summary = "根据用户ID查询角色列表")
    @GetMapping("/listByUser/{userId}")
    public Resp<List<RoleVO>> listRolesByUserId(@PathVariable("userId") @Valid @NotBlank String userId) {
        return Resp.success(userRoleService.listRolesByUserId(userId));
    }

    @Operation(summary = "删除用户所有角色关联")
    @DeleteMapping("/deleteByUser/{userId}")
    public Resp<Boolean> deleteByUserId(@PathVariable("userId") @Valid @NotBlank String userId) {
        return Resp.success(userRoleService.deleteByUserId(userId));
    }

    @Operation(summary = "删除用户指定角色关联")
    @DeleteMapping("/deleteByUserAndRoles/{userId}")
    public Resp<Boolean> deleteByUserIdAndRoleIds(@PathVariable("userId") @Valid @NotBlank String userId,
                                                  @RequestBody @Valid @NotEmpty List<@NotBlank String> roleIds) {
        return Resp.success(userRoleService.deleteByUserIdAndRoleIds(userId, roleIds));
    }
}