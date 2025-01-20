package com.oneself.dept.controller;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
//@LogRequestDetails
@RequireLogin
@RestController
@RequestMapping({"/dept"})
public class DeptController {
    private final DeptService deptService;

    @Autowired
    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "新增部门")
    @PostMapping({"/add"})
    public ResponseVO<Boolean> add(@RequestBody DeptDTO dto) {
        Integer size = deptService.addDept(dto);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("新增部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "根据 ID 查询部门")
    @GetMapping({"/get/{id}"})
    public ResponseVO<DeptVO> get(@PathVariable("id") Long id) {
        return ResponseVO.success(deptService.getDept(id));
    }

    @Operation(summary = "修改部门")
    @PutMapping({"/update/{id}"})
    public ResponseVO<Boolean> update(@PathVariable("id") Long id, @RequestBody DeptDTO dto) {
        Integer size = deptService.updateDept(id, dto);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("更新部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "删除部门")
    @DeleteMapping({"/delete"})
    public ResponseVO<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotNull Long> ids) {
        Integer size = deptService.deleteDept(ids);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("删除部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "更新部门状态")
    @PutMapping({"/update/status/{status}"})
    public ResponseVO<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotNull Long> ids, @PathVariable("status") StatusEnum status) {
        Integer updateStatus = deptService.updateStatus(ids, status);
        if (ObjectUtils.isEmpty(updateStatus)) {
            return ResponseVO.failure("更新部门状态失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "查询部门列表")
    @PostMapping({"/page/list"})
    public PageVO<DeptVO> pageList(@RequestBody PageDTO<PageDeptDTO> dto) {
        return deptService.pageList(dto);
    }

    @Operation(summary = "查询部门树")
    @GetMapping({"/get/tree"})
    public ResponseVO<List<DeptTreeVO>> getTree() {
        return deptService.getTree();
    }
}
