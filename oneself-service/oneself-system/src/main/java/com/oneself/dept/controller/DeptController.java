package com.oneself.dept.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.model.dto.DeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.vo.DeptTreeVO;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.dept.service.DeptService;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
@RequestLogging
@RequireLogin
@RestController
@RequestMapping({"/dept"})
public class DeptController {
    private final DeptService deptService;

    @Operation(summary = "新增部门")
    @PostMapping
    public ResponseVO<Boolean> add(@RequestBody @Valid DeptDTO dto) {
        Integer size = deptService.add(dto);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("新增部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "根据 ID 查询部门")
    @GetMapping("/{id}")
    public ResponseVO<DeptVO> get(@PathVariable("id") @Valid @NotNull @Positive Long id) {
        return ResponseVO.success(deptService.get(id));
    }

    @Operation(summary = "修改部门")
    @PutMapping("/{id}")
    public ResponseVO<Boolean> update(@PathVariable("id") @Valid @NotNull @Positive Long id, @RequestBody @Valid DeptDTO dto) {
        // 1. 更新当前部门
        Integer size = deptService.update(id, dto);
        if (ObjectUtils.isEmpty(size)) {
            // 2. 更新部门及子部门状态
            deptService.updateStatus(Collections.singletonList(id), dto.getStatus());
            return ResponseVO.failure("更新部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "删除部门")
    @DeleteMapping
    public ResponseVO<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotNull Long> ids) {
        Integer size = deptService.delete(ids);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("删除部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "更新部门状态")
    @PutMapping("/status/{status}")
    public ResponseVO<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotNull Long> ids, @PathVariable("status") @Valid @NotNull StatusEnum status) {
        Integer updateStatus = deptService.updateStatus(ids, status);
        if (ObjectUtils.isEmpty(updateStatus)) {
            return ResponseVO.failure("更新部门状态失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "查询部门列表")
    @PostMapping("/page")
    public PageVO<DeptVO> page(@RequestBody @Valid PageDTO<PageDeptDTO> dto) {
        return deptService.page(dto);
    }

    @Operation(summary = "查询部门树")
    @GetMapping("/tree")
    public ResponseVO<List<DeptTreeVO>> tree() {
        return deptService.tree();
    }
}
