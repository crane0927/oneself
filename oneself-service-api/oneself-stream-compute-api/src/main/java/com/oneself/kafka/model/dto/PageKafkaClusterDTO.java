package com.oneself.kafka.model.dto;

import com.oneself.kafka.model.enums.KafkaClusterStatusEnum;
import com.oneself.kafka.model.enums.KafkaSecurityProtocolEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/4/28
 * packageName com.oneself.kafka.model.dto
 * className PageKafkaClusterDTO
 * description
 * version 1.0
 */
@Data
public class PageKafkaClusterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "集群名称")
    private String name;
    @Schema(description = "集群描述")
    private String description;
    @Schema(description = "集群状态")
    private KafkaClusterStatusEnum status;
    @Schema(description = "认证机制类型")
    private KafkaSecurityProtocolEnum securityProtocol;
}

