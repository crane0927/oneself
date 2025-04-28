package com.oneself.kafka.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.kafka.model.enums.KafkaClusterStatusEnum;
import com.oneself.kafka.model.enums.KafkaSecurityProtocolEnum;
import com.oneself.model.pojo.BasePojo;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * @author liuhuan
 * date 2025/4/28
 * packageName com.oneself.kafka.model.pojo
 * className KafkaCluster
 * description Kafka 集群
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("kafka_cluster")
public class KafkaCluster extends BasePojo {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("bootstrap_servers")
    private String bootstrapServers;

    @TableField("kafka_version")
    private String kafkaVersion;

    @TableField("enable_auth")
    private Boolean enableAuth;

    @TableField("security_protocol")
    private KafkaSecurityProtocolEnum securityProtocol;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("status")
    private KafkaClusterStatusEnum status;

}
