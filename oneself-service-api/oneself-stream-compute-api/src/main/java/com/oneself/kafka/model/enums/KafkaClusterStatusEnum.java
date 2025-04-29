package com.oneself.kafka.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    private final String desc;

    @JsonCreator
    public static KafkaClusterStatusEnum fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (KafkaClusterStatusEnum status : KafkaClusterStatusEnum.values()) {
            if (String.valueOf(status.code).equals(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("KafkaClusterStatusEnum 的值无效: " + value);
    }
}
