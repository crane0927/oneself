package com.oneself.script.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.script.model.dto.PageQueryDTO;
import com.oneself.script.model.dto.ScriptDTO;
import com.oneself.script.model.vo.ScriptVO;
import com.oneself.script.service.ScriptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/8/27
 * packageName com.oneself.script.controller
 * className ScriptController
 * description 脚本管理
 * version 1.0
 */
@Tag(name = "脚本管理")
@Slf4j
@RequiredArgsConstructor
@RequireLogin
@RequestLogging
@RestController
@RefreshScope
@RequestMapping({"/script"})
public class ScriptController {

    private final ScriptService scriptService;

    @Operation(summary = "新增")
    @PostMapping
    public ResponseVO<Long> add(@RequestBody @Valid ScriptDTO dto) {
        return ResponseVO.success(scriptService.add(dto));
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    public ResponseVO<Boolean> edit(@PathVariable("id") @Valid @NotNull @Positive Long id, @RequestBody @Valid ScriptDTO dto) {
        return ResponseVO.success(scriptService.edit(id, dto));
    }

    @Operation(summary = "查询列表")
    @PostMapping("/page")
    public PageVO<ScriptVO> page(@RequestBody @Valid PageDTO<PageQueryDTO> dto) {
        return scriptService.page(dto);
    }

    @Operation(summary = "查询列表")
    @GetMapping("/{id}")
    public ResponseVO<ScriptVO> get(@PathVariable("id") @Valid @NotNull @Positive Long id) {
        return ResponseVO.success(scriptService.get(id));
    }

    @Operation(summary = "批量删除")
    @DeleteMapping
    public ResponseVO<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotNull Long> ids) {
        return ResponseVO.success(scriptService.delete(ids));
    }
}
