package com.oneself.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.PermissionDTO;
import com.oneself.model.dto.PermissionQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.PermissionTreeVO;
import com.oneself.model.vo.PermissionVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.service.PermissionService;
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
 * className PermissionController
 * description
 * version 1.0
 */
@Tag(name = "权限信息")
@Slf4j
@RequiredArgsConstructor
@RequestLogging
@RequireLogin
@RestController
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "新增权限")
    @PostMapping
    public ResponseVO<String> add(@RequestBody @Valid PermissionDTO dto) {
        return ResponseVO.success(permissionService.add(dto));
    }

    @Operation(summary = "根据ID查询权限")
    @GetMapping("/{id}")
    public ResponseVO<PermissionVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return ResponseVO.success(permissionService.get(id));
    }

    @Operation(summary = "修改权限")
    @PutMapping("/{id}")
    public ResponseVO<Boolean> update(@PathVariable("id") @Valid @NotBlank String id,
                                      @RequestBody @Valid PermissionDTO dto) {
        return ResponseVO.success(permissionService.update(id, dto));
    }

    @Operation(summary = "删除权限")
    @DeleteMapping
    public ResponseVO<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return ResponseVO.success(permissionService.delete(ids));
    }

    @Operation(summary = "更新权限状态")
    @PutMapping("/status/{status}")
    public ResponseVO<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids,
                                            @PathVariable("status") @Valid @NotBlank StatusEnum status) {
        return ResponseVO.success(permissionService.updateStatus(ids, status));
    }

    @Operation(summary = "分页查询权限")
    @PostMapping("/page")
    public PageVO<PermissionVO> page(@RequestBody @Valid PageDTO<PermissionQueryDTO> dto) {
        return permissionService.page(dto);
    }

    @Operation(summary = "查询权限树")
    @GetMapping("/tree")
    public ResponseVO<List<PermissionTreeVO>> tree() {
        return ResponseVO.success(permissionService.tree());
    }

    @Operation(summary = "根据角色ID查询权限列表")
    @GetMapping("/listByRole/{roleId}")
    public ResponseVO<List<PermissionVO>> listByRoleId(@PathVariable("roleId") @Valid @NotBlank String roleId) {
        return ResponseVO.success(permissionService.listByRoleId(roleId));
    }
}
