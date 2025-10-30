package com.oneself.kafka.service;

import com.oneself.kafka.model.dto.KafkaClusterDTO;
import com.oneself.kafka.model.dto.PageKafkaClusterDTO;
import com.oneself.kafka.model.vo.KafkaClusterVO;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * @author liuhuan
 * date 2025/4/27
 * packageName com.oneself.kafka.service
 * interfaceName KafkaClusterService
 * description Kafka 集群接口
 * version 1.0
 */
public interface KafkaClusterService {
    Integer add(@Valid KafkaClusterDTO dto);

    Integer update(@Valid @NotNull @Positive Long id, @Valid KafkaClusterDTO dto);

    PageResp<KafkaClusterVO> page(@Valid PageReq<PageKafkaClusterDTO> dto);
}
