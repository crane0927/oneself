package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.vo.PermissionVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.service.RolePermissionService;
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
 * className RolePermissionController
 * description
 * version 1.0
 */
@Tag(name = "角色权限关联")
@Slf4j
@RequiredArgsConstructor
@ApiLog
@RestController
@RequestMapping("/roles")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @Operation(summary = "给角色分配权限")
    @PostMapping("/{roleId}/permissions")
    public ResponseVO<Boolean> assignPermissions(@PathVariable("roleId") @Valid @NotBlank String roleId,
                                                 @RequestBody @Valid @NotEmpty List<@NotBlank String> permIds) {
        return ResponseVO.success(rolePermissionService.assignPermissions(roleId, permIds));
    }

    @Operation(summary = "根据角色ID查询权限列表")
    @GetMapping("/{roleId}/permissions")
    public ResponseVO<List<PermissionVO>> listPermissionsByRoleId(@PathVariable("roleId") @Valid @NotBlank String roleId) {
        return ResponseVO.success(rolePermissionService.listPermissionsByRoleId(roleId));
    }

    @Operation(summary = "删除角色所有权限关联")
    @DeleteMapping("/{roleId}/permissions")
    public ResponseVO<Boolean> deleteByRoleId(@PathVariable("roleId") @Valid @NotBlank String roleId) {
        return ResponseVO.success(rolePermissionService.deleteByRoleId(roleId));
    }

    @Operation(summary = "删除角色指定权限关联")
    @DeleteMapping("/{roleId}/permissions/batch")
    public ResponseVO<Boolean> deleteByRoleIdAndPermIds(@PathVariable("roleId") @Valid @NotBlank String roleId,
                                                        @RequestBody @Valid @NotEmpty List<@NotBlank String> permIds) {
        return ResponseVO.success(rolePermissionService.deleteByRoleIdAndPermIds(roleId, permIds));
    }
}