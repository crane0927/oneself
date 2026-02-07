package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.dto.PermissionDTO;
import com.oneself.model.dto.PermissionQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.PermissionTreeVO;
import com.oneself.model.vo.PermissionVO;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import com.oneself.resp.Resp;
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
@ApiLog
@RestController
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "新增权限")
    @PostMapping
    public Resp<String> add(@RequestBody @Valid PermissionDTO dto) {
        return Resp.success(permissionService.add(dto));
    }

    @Operation(summary = "根据ID查询权限")
    @GetMapping("/{id}")
    public Resp<PermissionVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return Resp.success(permissionService.get(id));
    }

    @Operation(summary = "修改权限")
    @PutMapping("/{id}")
    public Resp<Boolean> update(@PathVariable("id") @Valid @NotBlank String id,
                                @RequestBody @Valid PermissionDTO dto) {
        return Resp.success(permissionService.update(id, dto));
    }

    @Operation(summary = "删除权限")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return Resp.success(permissionService.delete(ids));
    }

    @Operation(summary = "更新权限状态")
    @PutMapping("/status/{status}")
    public Resp<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids,
                                      @PathVariable("status") @Valid @NotBlank StatusEnum status) {
        return Resp.success(permissionService.updateStatus(ids, status));
    }

    @Operation(summary = "分页查询权限")
    @PostMapping("/page")
    public PageResp<PermissionVO> page(@RequestBody @Valid PageReq<PermissionQueryDTO> dto) {
        return permissionService.page(dto);
    }

    @Operation(summary = "查询权限树")
    @GetMapping("/tree")
    public Resp<List<PermissionTreeVO>> tree() {
        return Resp.success(permissionService.tree());
    }

    @Operation(summary = "根据角色ID查询权限列表（原则 6 名词化路径）")
    @GetMapping("/by-role/{roleId}")
    public Resp<List<PermissionVO>> listByRoleId(@PathVariable("roleId") @Valid @NotBlank String roleId) {
        return Resp.success(permissionService.listByRoleId(roleId));
    }

    /** @deprecated 请使用 GET /permission/by-role/{roleId} */
    @Deprecated
    @Operation(summary = "根据角色ID查询权限列表（已废弃）", hidden = true)
    @GetMapping("/listByRole/{roleId}")
    public Resp<List<PermissionVO>> listByRoleIdLegacy(@PathVariable("roleId") @Valid @NotBlank String roleId) {
        return Resp.success(permissionService.listByRoleId(roleId));
    }
}
