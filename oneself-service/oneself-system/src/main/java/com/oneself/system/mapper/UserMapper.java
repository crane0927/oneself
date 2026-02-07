package com.oneself.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.system.model.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/9/2
 * packageName com.oneself.mapper
 * interfaceName UserMapper
 * description 用户表映射
 * version 1.0
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
}
