package com.oneself.dept.controller;

import com.oneself.annotation.LogRequestDetails;
import com.oneself.annotation.RequireLogin;
import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.model.dto.AddDeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.dept.service.DeptService;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@LogRequestDetails
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
    public ResponseVO<Boolean> add(@RequestBody AddDeptDTO dto) {
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

    @Operation(summary = "更新部门")
    @PutMapping({"/update"})
    public ResponseVO<Boolean> update(@RequestBody AddDeptDTO dto) {
        Integer size = deptService.updateDept(dto);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("更新部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "删除部门")
    @DeleteMapping({"/delete/{id}"})
    public ResponseVO<Boolean> delete(@PathVariable("id") Long id) {
        Integer size = deptService.deleteDept(id);
        if (ObjectUtils.isEmpty(size)) {
            return ResponseVO.failure("删除部门失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "更新部门状态")
    @PutMapping({"/update/status/{id}/{status}"})
    public ResponseVO<Boolean> updateStatus(@PathVariable("id") Long id, @PathVariable("status") StatusEnum status) {
        Integer updateStatus = deptService.updateStatus(id, status);
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
}
