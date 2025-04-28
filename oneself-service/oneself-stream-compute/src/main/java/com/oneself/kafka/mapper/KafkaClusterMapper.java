package com.oneself.kafka.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.kafka.model.pojo.KafkaCluster;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/4/28
 * packageName com.oneself.kafka.mapper
 * interfaceName KafkaClusterMapper
 * description Kafka 集群表映射
 * version 1.0
 */
@Mapper
@Repository
public interface KafkaClusterMapper extends BaseMapper<KafkaCluster> {
}
