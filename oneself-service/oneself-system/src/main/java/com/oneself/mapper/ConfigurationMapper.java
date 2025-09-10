package com.oneself.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.model.pojo.Configuration;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.mapper
 * interfaceName ConfigurationMapper
 * description
 * version 1.0
 */
@Mapper
@Repository
public interface ConfigurationMapper extends BaseMapper<Configuration> {
}
