package com.oneself.kafka.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.kafka.mapper.KafkaClusterMapper;
import com.oneself.kafka.model.dto.KafkaClusterDTO;
import com.oneself.kafka.model.dto.PageKafkaClusterDTO;
import com.oneself.kafka.model.pojo.KafkaCluster;
import com.oneself.kafka.model.vo.KafkaClusterVO;
import com.oneself.kafka.service.KafkaClusterService;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.utils.AssertUtils;
import com.oneself.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author liuhuan
 * date 2025/4/27
 * packageName com.oneself.kafka.service.impl
 * className KafkaClusterServiceImpl
 * description Kafka 集群接口实现类
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaClusterServiceImpl implements KafkaClusterService {

    private final KafkaClusterMapper kafkaClusterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(KafkaClusterDTO dto) {
        AssertUtils.notNull(dto, "参数对象不能为空");
        // 1. 查询集群名称是否重复
        Long count = kafkaClusterMapper.selectCount(
                new LambdaQueryWrapper<KafkaCluster>()
                        .eq(KafkaCluster::getName, dto.getName())
        );

        AssertUtils.isFalse(count != null && count > 0, "集群名称已存在");

        // 2. 新建 KafkaCluster 对象并拷贝属性
        KafkaCluster kafkaCluster = KafkaCluster.builder().build();
        BeanCopyUtils.copy(dto, kafkaCluster);

        // 3. 插入数据
        return kafkaClusterMapper.insert(kafkaCluster);
    }

    @Override
    public Integer update(Long id, KafkaClusterDTO dto) {
        // 简洁版校验
        AssertUtils.notNull(id, "id不能为空");
        AssertUtils.notNull(dto, "参数对象不能为空");

        KafkaCluster existingCluster = kafkaClusterMapper.selectById(id);
        AssertUtils.notNull(existingCluster, "更新失败，未找到对应的集群");

        Long count = kafkaClusterMapper.selectCount(
                new LambdaQueryWrapper<KafkaCluster>()
                        .eq(KafkaCluster::getName, dto.getName())
                        .ne(KafkaCluster::getId, id)
        );
        AssertUtils.isTrue(count == 0, "集群名称已存在");

        KafkaCluster updatedCluster = KafkaCluster.builder().id(id).build();
        BeanCopyUtils.copy(dto, updatedCluster);

        return kafkaClusterMapper.updateById(updatedCluster);
    }

    @Override
    public PageVO<KafkaClusterVO> page(PageDTO<PageKafkaClusterDTO> dto) {
        PageKafkaClusterDTO condition = dto.getCondition();
        PageDTO.Pagination pagination = dto.getPagination();
        Page<KafkaCluster> pageRequest = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaQueryWrapper<KafkaCluster> wrapper = new LambdaQueryWrapper<KafkaCluster>()
                .like(StringUtils.isNotBlank(condition.getName()), KafkaCluster::getName, condition.getName())
                .like(StringUtils.isNotBlank(condition.getDescription()), KafkaCluster::getDescription, condition.getDescription())
                .eq(Optional.ofNullable(condition.getStatus()).isPresent(), KafkaCluster::getStatus, condition.getStatus())
                .eq(Optional.ofNullable(condition.getSecurityProtocol()).isPresent(), KafkaCluster::getSecurityProtocol, condition.getSecurityProtocol());

        Page<KafkaCluster> kafkaClusterPage = kafkaClusterMapper.selectPage(pageRequest, wrapper);
        return PageVO.convert(kafkaClusterPage, kafkaCluster -> {
            KafkaClusterVO kafkaClusterVO = new KafkaClusterVO();
            BeanCopyUtils.copy(kafkaCluster, kafkaClusterVO);
            return kafkaClusterVO;
        });
    }
}
