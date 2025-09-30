package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneself.mapper.RoleMapper;
import com.oneself.mapper.UserRoleMapper;
import com.oneself.model.pojo.Role;
import com.oneself.model.pojo.UserRole;
import com.oneself.model.vo.RoleVO;
import com.oneself.service.UserRoleService;
import com.oneself.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.service.impl
 * className UserRoleServiceImpl
 * description
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 分配是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(String userId, List<String> roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }

        // 检查角色是否存在
        List<Role> roles = roleMapper.selectByIds(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException("部分角色不存在");
        }

        // 先删除用户现有的角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));

        // 批量插入新的角色关联
        List<UserRole> userRoles = roleIds.stream()
            .map(roleId -> UserRole.builder()
                .userId(userId)
                .roleId(roleId)
                .build())
            .toList();

        // 批量插入
        for (UserRole userRole : userRoles) {
            userRoleMapper.insert(userRole);
        }

        log.info("用户角色分配成功, 用户ID: {}, 角色数量: {}", userId, roleIds.size());
        return true;
    }

    /**
     * 根据用户ID获取用户所拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表 VO
     */
    @Override
    public List<RoleVO> listRolesByUserId(String userId) {
        // 查询用户角色关联
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );

        if (ObjectUtils.isEmpty(userRoles)) {
            return List.of();
        }

        // 获取角色ID列表
        List<String> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());

        // 查询角色信息
        List<Role> roles = roleMapper.selectByIds(roleIds);

        // 转换为VO
        return roles.stream()
            .map(role -> {
                RoleVO vo = new RoleVO();
                BeanCopyUtils.copy(role, vo);
                return vo;
            })
            .collect(Collectors.toList());
    }

    /**
     * 删除用户下的所有角色关联
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(String userId) {
        int deleteCount = userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );

        log.info("删除用户所有角色关联成功, 用户ID: {}, 删除数量: {}", userId, deleteCount);
        return true;
    }

    /**
     * 删除用户指定角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserIdAndRoleIds(String userId, List<String> roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }

        int deleteCount = userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds)
        );

        log.info("删除用户指定角色关联成功, 用户ID: {}, 角色数量: {}, 删除数量: {}", userId, roleIds.size(), deleteCount);
        return true;
    }
}
