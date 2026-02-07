package com.oneself.system.controller;

import com.oneself.common.feature.sensitive.annotation.ApiLog;
import com.oneself.system.model.dto.RoleDTO;
import com.oneself.system.model.dto.RoleQueryDTO;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.system.model.vo.RoleTreeVO;
import com.oneself.system.model.vo.RoleVO;
import com.oneself.common.feature.web.req.PageReq;
import com.oneself.common.feature.web.resp.PageResp;
import com.oneself.common.core.resp.Resp;
import com.oneself.system.service.RoleService;
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
 * className RoleController
 * description
 * version 1.0
 */
@Tag(name = "角色信息")
@Slf4j
@RequiredArgsConstructor
@ApiLog
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "新增角色")
    @PostMapping
    public Resp<String> add(@RequestBody @Valid RoleDTO dto) {
        return Resp.success(roleService.add(dto));
    }

    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public Resp<RoleVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return Resp.success(roleService.get(id));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    public Resp<Boolean> update(@PathVariable("id") @Valid @NotBlank String id,
                                @RequestBody @Valid RoleDTO dto) {
        return Resp.success(roleService.update(id, dto));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return Resp.success(roleService.delete(ids));
    }

    @Operation(summary = "更新角色状态")
    @PutMapping("/status/{status}")
    public Resp<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids,
                                      @PathVariable("status") @Valid @NotBlank StatusEnum status) {
        return Resp.success(roleService.updateStatus(ids, status));
    }

    @Operation(summary = "分页查询角色")
    @PostMapping("/page")
    public PageResp<RoleVO> page(@RequestBody @Valid PageReq<RoleQueryDTO> dto) {
        return roleService.page(dto);
    }

    @Operation(summary = "查询所有角色列表")
    @GetMapping("/all")
    public Resp<List<RoleVO>> listAll() {
        return Resp.success(roleService.listAll());
    }

    /** @deprecated 请使用 GET /role/all */
    @Deprecated
    @Operation(summary = "查询所有角色列表（已废弃）", hidden = true)
    @GetMapping("/list/all")
    public Resp<List<RoleVO>> listAllLegacy() {
        return Resp.success(roleService.listAll());
    }

    @Operation(summary = "查询角色树（RBAC1：支持角色继承）")
    @GetMapping("/tree")
    public Resp<List<RoleTreeVO>> tree() {
        return Resp.success(roleService.tree());
    }
}
