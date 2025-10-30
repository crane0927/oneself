package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.dto.ConfigurationDTO;
import com.oneself.model.dto.ConfigurationQueryDTO;
import com.oneself.req.PageReq;
import com.oneself.model.vo.ConfigurationVO;
import com.oneself.resp.PageResp;
import com.oneself.resp.Resp;
import com.oneself.service.ConfigurationService;
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
 * date 2025/9/10
 * packageName com.oneself.controller
 * className ConfigurationController
 * description
 * version 1.0
 */
@Tag(name = "参数配置")
@Slf4j
@RequiredArgsConstructor
@ApiLog
@RestController
@RequestMapping({"/configuration"})
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Operation(summary = "新增")
    @PostMapping
    public Resp<String> add(@RequestBody @Valid ConfigurationDTO dto) {
        return Resp.success(configurationService.add(dto));
    }

    @Operation(summary = "根据 ID 查询")
    @GetMapping("/{id}")
    public Resp<ConfigurationVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return Resp.success(configurationService.get(id));
    }

    @Operation(summary = "修改")
    @PutMapping("/{id}")
    public Resp<Boolean> update(@PathVariable("id") @Valid @NotBlank String id, @RequestBody @Valid ConfigurationDTO dto) {
        return Resp.success(configurationService.update(id, dto));
    }

    @Operation(summary = "删除")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return Resp.success(configurationService.delete(ids));
    }



    @Operation(summary = "查询列表")
    @PostMapping("/page")
    public PageResp<ConfigurationVO> page(@RequestBody @Valid PageReq<ConfigurationQueryDTO> dto) {
        return configurationService.page(dto);
    }
}
