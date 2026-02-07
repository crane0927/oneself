package com.oneself.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.system.model.pojo.Constraint;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.mapper
 * interfaceName ConstraintMapper
 * description 约束配置表映射（RBAC2）
 * version 1.0
 */
@Mapper
@Repository
public interface ConstraintMapper extends BaseMapper<Constraint> {
}

