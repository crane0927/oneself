package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneself.mapper.PermissionMapper;
import com.oneself.mapper.RolePermissionMapper;
import com.oneself.model.pojo.Permission;
import com.oneself.model.pojo.RolePermission;
import com.oneself.model.vo.PermissionVO;
import com.oneself.service.ConstraintService;
import com.oneself.service.RolePermissionService;
import com.oneself.service.RoleService;
import com.oneself.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.service.impl
 * className RolePermissionServiceImpl
 * description
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final RoleService roleService;
    private final ConstraintService constraintService;
    /**
     * 给角色分配权限
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     * @return 分配是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysRolePermission", key = "#roleId")
    public boolean assignPermissions(String roleId, List<String> permIds) {
        if (ObjectUtils.isEmpty(permIds)) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }

        // 检查权限是否存在
        List<Permission> permissions = permissionMapper.selectByIds(permIds);
        if (permissions.size() != permIds.size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }

        // RBAC2: 检查权限约束
        constraintService.checkPermissionMutexConstraint(permIds); // 权限互斥约束
        constraintService.checkRolePermissionCardinalityConstraint(roleId, permIds); // 角色权限基数约束

        // 先删除角色现有的权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));

        // 批量插入新的权限关联
        List<RolePermission> rolePermissions = permIds.stream()
            .map(permId -> RolePermission.builder()
                .roleId(roleId)
                .permId(permId)
                .build())
            .toList();

        // 批量插入
        for (RolePermission rolePermission : rolePermissions) {
            rolePermissionMapper.insert(rolePermission);
        }

        log.info("角色权限分配成功, 角色ID: {}, 权限数量: {}", roleId, permIds.size());
        return true;
    }

    /**
     * 根据角色ID查询权限列表（RBAC1：包含继承的权限）
     *
     * @param roleId 角色ID
     * @return 权限列表 VO（包括该角色直接分配的权限和从父角色继承的权限）
     */
    @Override
    @Cacheable(value = "sysRolePermission", key = "#roleId")
    public List<PermissionVO> listPermissionsByRoleId(String roleId) {
        // RBAC1: 获取该角色的所有父角色（包括自身），用于权限继承
        Set<String> allRoleIds = roleService.getAllParentRoleIds(roleId);

        // 查询所有角色（包括继承的角色）的权限关联
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, allRoleIds)
        );

        if (ObjectUtils.isEmpty(rolePermissions)) {
            return List.of();
        }

        // 获取权限ID列表（去重）
        Set<String> permIdSet = rolePermissions.stream()
            .map(RolePermission::getPermId)
            .collect(Collectors.toSet());

        // 查询权限信息
        List<Permission> permissions = permissionMapper.selectByIds(new ArrayList<>(permIdSet));

        // 转换为VO
        return permissions.stream()
            .map(permission -> {
                PermissionVO vo = new PermissionVO();
                BeanCopyUtils.copy(permission, vo);
                return vo;
            })
            .collect(Collectors.toList());
    }

    /**
     * 删除角色下的所有权限关联
     *
     * @param roleId 角色ID
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysRolePermission", key = "#roleId")
    public boolean deleteByRoleId(String roleId) {
        int deleteCount = rolePermissionMapper.delete(
            new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId)
        );

        log.info("删除角色所有权限关联成功, 角色ID: {}, 删除数量: {}", roleId, deleteCount);
        return true;
    }

    /**
     * 删除角色指定权限关联
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysRolePermission", key = "#roleId")
    public boolean deleteByRoleIdAndPermIds(String roleId, List<String> permIds) {
        if (ObjectUtils.isEmpty(permIds)) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }

        int deleteCount = rolePermissionMapper.delete(
            new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId)
                .in(RolePermission::getPermId, permIds)
        );

        log.info("删除角色指定权限关联成功, 角色ID: {}, 权限数量: {}, 删除数量: {}", roleId, permIds.size(), deleteCount);
        return true;
    }
}
