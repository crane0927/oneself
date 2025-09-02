package com.oneself.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.model.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/9/2
 * packageName com.oneself.mapper
 * interfaceName UserRoleMapper
 * description 用户角色关联表映射
 * version 1.0
 */
@Mapper
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
