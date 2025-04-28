package com.oneself.kafka.model.vo;

import com.oneself.annotation.Sensitive;
import com.oneself.kafka.model.enums.KafkaClusterStatusEnum;
import com.oneself.kafka.model.enums.KafkaSecurityProtocolEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/4/28
 * packageName com.oneself.kafka.model.vo
 * className KafkaClusterVO
 * description Kafka 集群VO
 * version 1.0
 */
@Data
@Schema(description = "Kafka 集群 VO")
public class KafkaClusterVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "集群 ID")
    private Long id;
    @Schema(description = "集群名称")
    private String name;
    @Schema(description = "集群描述")
    private String description;
    @Schema(description = "集群连接地址")
    private String bootstrapServers;
    @Schema(description = "集群版本")
    private String kafkaVersion;
    @Schema(description = "是否启用认证")
    private Boolean enableAuth;
    @Schema(description = "认证机制")
    private KafkaSecurityProtocolEnum securityProtocol;
    @Schema(description = "认证用户名")
    private String username;

    @Sensitive
    @Schema(description = "认证密码")
    private String password;

    @Schema(description = "集群状态")
    private KafkaClusterStatusEnum status;
    @Schema(description = "创建人")
    private String createBy;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "修改人")
    private String updateBy;
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
