package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.DeptDTO;
import com.oneself.model.dto.DeptQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.DeptTreeVO;
import com.oneself.model.vo.DeptVO;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import com.oneself.resp.Resp;
import com.oneself.service.DeptService;
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
 * date 2025/1/18
 * packageName com.oneself.dept.controller
 * className DeptController
 * description 部门信息
 * version 1.0
 */
@Tag(name = "部门信息")
@Slf4j
@RequiredArgsConstructor
@ApiLog
@RequireLogin
@RestController
@RequestMapping({"/dept"})
public class DeptController {
    private final DeptService deptService;

    @Operation(summary = "新增")
    @PostMapping
    public Resp<String> add(@RequestBody @Valid DeptDTO dto) {
        return Resp.success(deptService.add(dto));
    }

//    @RequirePermission(value = {"sys:dept:get"})
//    @RequireRoles(value = {"admin"})
    @Operation(summary = "根据 ID 查询")
    @GetMapping("/{id}")
    public Resp<DeptVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return Resp.success(deptService.get(id));
    }

    @Operation(summary = "修改")
    @PutMapping("/{id}")
    public Resp<Boolean> update(@PathVariable("id") @Valid @NotBlank String id, @RequestBody @Valid DeptDTO dto) {
        return Resp.success(deptService.update(id, dto));
    }

    @Operation(summary = "删除")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return Resp.success(deptService.delete(ids));
    }

    @Operation(summary = "更新状态")
    @PutMapping("/status/{status}")
    public Resp<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids, @PathVariable("status") @Valid StatusEnum status) {
        return Resp.success(deptService.updateStatus(ids, status));
    }

    @Operation(summary = "查询列表")
    @PostMapping("/page")
    public PageResp<DeptVO> page(@RequestBody @Valid PageReq<DeptQueryDTO> dto) {
        return deptService.page(dto);
    }

    @Operation(summary = "查询树")
    @GetMapping("/tree")
    public Resp<List<DeptTreeVO>> tree() {
        return Resp.success(deptService.tree());
    }

    @Operation(summary = "查询所有部门列表（原则 6 名词化路径）")
    @GetMapping("/all")
    public Resp<List<DeptVO>> listAll() {
        return Resp.success(deptService.listAll());
    }

    /** @deprecated 请使用 GET /dept/all */
    @Deprecated
    @Operation(summary = "查询所有部门列表（已废弃）", hidden = true)
    @GetMapping("/list/all")
    public Resp<List<DeptVO>> listAllLegacy() {
        return Resp.success(deptService.listAll());
    }
}
