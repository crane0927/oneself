package com.oneself.kafka.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liuhuan
 * date 2025/4/27
 * packageName com.oneself.kafka.model.enums
 * enumName KafkaSecurityProtocolEnum
 * description Kafka 安全协议枚举
 * version 1.0
 */
@Getter
@AllArgsConstructor
public enum KafkaSecurityProtocolEnum {
    PLAINTEXT("PLAINTEXT", "无认证"),
    SASL_PLAINTEXT("SASL_PLAINTEXT", "SASL + 明文传输"),
    SASL_SSL("SASL_SSL", "SASL + SSL加密"),
    SSL("SSL", "纯SSL加密");

    private final String code;
    private final String description;
}
