package com.oneself.kafka.model.dto;

import com.oneself.annotation.Sensitive;
import com.oneself.kafka.model.enums.KafkaClusterStatusEnum;
import com.oneself.kafka.model.enums.KafkaSecurityProtocolEnum;
import com.oneself.model.enums.DesensitizedTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/4/27
 * packageName com.oneself.kafka.model.dto
 * className KafkaClusterDTO
 * description Kafka 集群 DTO
 * version 1.0
 */
@Data
@Schema(description = "Kafka 集群信息")
public class KafkaClusterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "集群名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "集群名称不能为空")
    @Size(max = 20, message = "集群名称长度不能超过 20 个字符")
    private String name;

    @Schema(description = "集群说明", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "集群说明不能为空")
    @Size(max = 200, message = "集群说明长度不能超过 200 个字符")
    private String description;

    @Schema(description = "Kafka 集群连接地址，如: 127.0.0.1:9092,127.0.0.2:9092")
    private String bootstrapServers;

    @Schema(description = "Kafka 版本号，例如 3.7.0")
    private String kafkaVersion;

    @Schema(description = "是否启用认证（true-启用，false-不启用）")
    private Boolean enableAuth;

    @Schema(description = "认证机制类型")
    private KafkaSecurityProtocolEnum securityProtocol;

    @Schema(description = "认证用户名")
    private String username;

    @Sensitive(DesensitizedTypeEnum.PASSWORD)
    @Schema(description = "认证密码")
    private String password;

    @Schema(description = "集群状态")
    private KafkaClusterStatusEnum status;


}