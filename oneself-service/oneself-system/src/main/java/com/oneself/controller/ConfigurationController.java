package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.dto.ConfigurationDTO;
import com.oneself.model.dto.ConfigurationQueryDTO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.ConfigurationVO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
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
    public ResponseVO<String> add(@RequestBody @Valid ConfigurationDTO dto) {
        return ResponseVO.success(configurationService.add(dto));
    }

    @Operation(summary = "根据 ID 查询")
    @GetMapping("/{id}")
    public ResponseVO<ConfigurationVO> get(@PathVariable("id") @Valid @NotBlank String id) {
        return ResponseVO.success(configurationService.get(id));
    }

    @Operation(summary = "修改")
    @PutMapping("/{id}")
    public ResponseVO<Boolean> update(@PathVariable("id") @Valid @NotBlank String id, @RequestBody @Valid ConfigurationDTO dto) {
        return ResponseVO.success(configurationService.update(id, dto));
    }

    @Operation(summary = "删除")
    @DeleteMapping
    public ResponseVO<Boolean> delete(@RequestBody @Valid @NotEmpty List<@NotBlank String> ids) {
        return ResponseVO.success(configurationService.delete(ids));
    }



    @Operation(summary = "查询列表")
    @PostMapping("/page")
    public PageVO<ConfigurationVO> page(@RequestBody @Valid PageDTO<ConfigurationQueryDTO> dto) {
        return configurationService.page(dto);
    }
}
