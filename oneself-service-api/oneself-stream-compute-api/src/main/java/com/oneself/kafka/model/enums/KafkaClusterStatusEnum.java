package com.oneself.kafka.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/4/27
 * packageName com.oneself.kafka.model.enums
 * enumName KafkaClusterStatusEnum
 * description Kafka 集群状态枚举
 * version 1.0
 */
@Getter
@AllArgsConstructor
public enum KafkaClusterStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String description;
}
