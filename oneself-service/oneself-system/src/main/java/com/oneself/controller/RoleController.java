package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.req.PageReq;
import com.oneself.model.dto.RoleDTO;
import com.oneself.model.dto.RoleQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.resp.PageResp;
import com.oneself.resp.Resp;
import com.oneself.model.vo.RoleVO;
import com.oneself.service.RoleService;
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
    @GetMapping("/list/all")
    public Resp<List<RoleVO>> listAll() {
        return Resp.success(roleService.listAll());
    }
}
