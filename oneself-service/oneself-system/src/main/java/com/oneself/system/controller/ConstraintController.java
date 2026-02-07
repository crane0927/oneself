package com.oneself.system.controller;

import com.oneself.common.feature.sensitive.annotation.ApiLog;
import com.oneself.system.model.dto.ConstraintDTO;
import com.oneself.system.model.dto.ConstraintQueryDTO;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.system.model.vo.ConstraintVO;
import com.oneself.common.feature.web.req.PageReq;
import com.oneself.common.feature.web.resp.PageResp;
import com.oneself.common.core.resp.Resp;
import com.oneself.system.service.ConstraintService;
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
 * date 2025/12/25
 * packageName com.oneself.controller
 * className ConstraintController
 * description 约束配置控制器（RBAC2）
 * version 1.0
 */
@Tag(name = "约束配置（RBAC2）")
@Slf4j
@RequiredArgsConstructor
@ApiLog
@RestController
@RequestMapping("/constraint")
public class ConstraintController {

    private final ConstraintService constraintService;

    @Operation(summary = "新增约束配置")
    @PostMapping
    public Resp<String> add(@RequestBody @Valid ConstraintDTO dto) {
        return Resp.success(constraintService.add(dto));
    }

    @Operation(summary = "根据ID查询约束配置")
    @GetMapping("/{id}")
    public Resp<ConstraintVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return Resp.success(constraintService.get(id));
    }

    @Operation(summary = "修改约束配置")
    @PutMapping("/{id}")
    public Resp<Boolean> update(@PathVariable("id") @Valid @NotBlank String id,
                                @RequestBody @Valid ConstraintDTO dto) {
        return Resp.success(constraintService.update(id, dto));
    }

    @Operation(summary = "删除约束配置")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return Resp.success(constraintService.delete(ids));
    }

    @Operation(summary = "更新约束配置状态")
    @PutMapping("/status/{status}")
    public Resp<Boolean> updateStatus(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids,
                                      @PathVariable("status") @Valid @NotBlank StatusEnum status) {
        return Resp.success(constraintService.updateStatus(ids, status));
    }

    @Operation(summary = "分页查询约束配置")
    @PostMapping("/page")
    public PageResp<ConstraintVO> page(@RequestBody @Valid PageReq<ConstraintQueryDTO> dto) {
        return constraintService.page(dto);
    }
}

