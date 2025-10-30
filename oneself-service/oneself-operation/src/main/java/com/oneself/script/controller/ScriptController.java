package com.oneself.script.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import com.oneself.resp.Resp;
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
@ApiLog
@RestController
@RefreshScope
@RequestMapping({"/scripts"})
public class ScriptController {

    private final ScriptService scriptService;

    @Operation(summary = "新增")
    @PostMapping
    public Resp<String> add(@RequestBody @Valid ScriptDTO dto) {
        return Resp.success(scriptService.add(dto));
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    public Resp<Boolean> edit(@PathVariable("id") @Valid @NotNull @Positive String id, @RequestBody @Valid ScriptDTO dto) {
        return Resp.success(scriptService.edit(id, dto));
    }

    @Operation(summary = "查询列表")
    @PostMapping("/page")
    public PageResp<ScriptVO> page(@RequestBody @Valid PageReq<PageQueryDTO> dto) {
        return scriptService.page(dto);
    }

    @Operation(summary = "根据 ID 查询")
    @GetMapping("/{id}")
    public Resp<ScriptVO> get(@PathVariable("id") @Valid @NotNull @Positive String id) {
        return Resp.success(scriptService.get(id));
    }

    @Operation(summary = "批量删除")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotNull String> ids) {
        return Resp.success(scriptService.delete(ids));
    }

}
