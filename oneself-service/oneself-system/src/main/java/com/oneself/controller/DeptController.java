package com.oneself.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.DeptDTO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.DeptQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.DeptTreeVO;
import com.oneself.model.vo.DeptVO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
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
@RequestLogging
@RequireLogin
@RestController
@RequestMapping({"/dept"})
public class DeptController {
    private final DeptService deptService;

    @Operation(summary = "新增")
    @PostMapping
    public ResponseVO<String> add(@RequestBody @Valid DeptDTO dto) {
        return ResponseVO.success(deptService.add(dto));
    }

    @Operation(summary = "根据 ID 查询")
    @GetMapping("/{id}")
    public ResponseVO<DeptVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return ResponseVO.success(deptService.get(id));
    }

    @Operation(summary = "修改")
    @PutMapping("/{id}")
    public ResponseVO<Boolean> update(@PathVariable("id") @Valid @NotBlank String id, @RequestBody @Valid DeptDTO dto) {
        return ResponseVO.success(deptService.update(id, dto));
    }

    @Operation(summary = "删除")
    @DeleteMapping
    public ResponseVO<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return ResponseVO.success(deptService.delete(ids));
    }

    @Operation(summary = "更新状态")
    @PutMapping("/status/{status}")
    public ResponseVO<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids, @PathVariable("status") @Valid @NotBlank StatusEnum status) {
        return ResponseVO.success(deptService.updateStatus(ids, status));
    }

    @Operation(summary = "查询列表")
    @PostMapping("/page")
    public PageVO<DeptVO> page(@RequestBody @Valid PageDTO<DeptQueryDTO> dto) {
        return deptService.page(dto);
    }

    @Operation(summary = "查询树")
    @GetMapping("/tree")
    public ResponseVO<List<DeptTreeVO>> tree() {
        return ResponseVO.success(deptService.tree());
    }
}
