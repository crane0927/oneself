package com.oneself.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.model.pojo.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/9/2
 * packageName com.oneself.mapper
 * interfaceName RolePermissionMapper
 * description 角色权限关联表映射
 * version 1.0
 */
@Mapper
@Repository
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
