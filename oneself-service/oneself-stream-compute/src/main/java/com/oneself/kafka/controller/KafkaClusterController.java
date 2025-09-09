package com.oneself.kafka.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.annotation.RequireLogin;
import com.oneself.kafka.model.dto.KafkaClusterDTO;
import com.oneself.kafka.model.dto.PageKafkaClusterDTO;
import com.oneself.kafka.model.vo.KafkaClusterVO;
import com.oneself.kafka.service.KafkaClusterService;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuhuan
 * date 2025/4/27
 * packageName com.oneself.kafka.controller
 * className KafkaClusterController
 * description Kafka 集群管理
 * version 1.0
 */
@Tag(name = "Kafka 集群")
@Slf4j
@RequiredArgsConstructor
@RequireLogin
@ApiLog
@RestController
@RequestMapping("/kafka/cluster")
public class KafkaClusterController {

    private final KafkaClusterService kafkaClusterService;

    @Operation(summary = "新增")
    @PostMapping
    public ResponseVO<Boolean> add(@RequestBody @Valid KafkaClusterDTO dto) {
        Integer size = kafkaClusterService.add(dto);
        if (size == null || size <= 0) {
            return ResponseVO.failure("新增集群失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "修改")
    @PutMapping("/{id}")
    public ResponseVO<Boolean> update(@PathVariable("id") @Valid @NotNull @Positive Long id, @RequestBody @Valid KafkaClusterDTO dto) {
        Integer size = kafkaClusterService.update(id, dto);
        if (size == null || size <= 0) {
            return ResponseVO.failure("修改集群失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "查询列表")
    @PostMapping("/page")
    public PageVO<KafkaClusterVO> page(@RequestBody @Valid PageDTO<PageKafkaClusterDTO> dto) {
        return kafkaClusterService.page(dto);
    }


}
