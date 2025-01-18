package com.oneself.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.user.model.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.mapper
 * interfaceName UserMapper
 * description 用户表 Mapper 映射
 * version 1.0
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
}
