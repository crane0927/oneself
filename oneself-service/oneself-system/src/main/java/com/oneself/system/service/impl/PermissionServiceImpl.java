package com.oneself.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.common.core.exception.OneselfException;
import com.oneself.system.mapper.PermissionMapper;
import com.oneself.system.mapper.RolePermissionMapper;
import com.oneself.system.model.dto.PermissionDTO;
import com.oneself.system.model.dto.PermissionQueryDTO;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.system.model.pojo.Permission;
import com.oneself.system.model.pojo.RolePermission;
import com.oneself.system.model.vo.PermissionTreeVO;
import com.oneself.system.model.vo.PermissionVO;
import com.oneself.common.infra.jdbc.pagination.MyBatisPageWrapper;
import com.oneself.common.feature.web.req.PageReq;
import com.oneself.common.feature.web.resp.PageResp;
import com.oneself.system.service.PermissionService;
import com.oneself.common.core.utils.BeanCopyUtils;
import com.oneself.common.infra.jdbc.utils.DuplicateCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.service.impl
 * className PermissionServiceImpl
 * description
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final CacheManager cacheManager;

    /**
     * 新增权限
     *
     * @param dto 权限信息 DTO，包含权限名称、权限编码、资源类型、资源路径等信息
     * @return 新增权限ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(PermissionDTO dto) {
        Permission permission = Permission.builder().build();
        BeanCopyUtils.copy(dto, permission);

        // 检查权限编码是否重复
        DuplicateCheckUtils.checkDuplicateMultiFields(
                permission,
                Permission::getId,
                permissionMapper::selectCount,
                "权限编码已存在",
                DuplicateCheckUtils.FieldCondition.of(Permission::getPermCode, DuplicateCheckUtils.ConditionType.EQ)
        );

        int insert = permissionMapper.insert(permission);
        if (insert == 0) {
            throw new OneselfException("新增权限失败");
        }
        log.info("权限添加成功, ID: {}", permission.getId());
        return permission.getId();
    }

    /**
     * 根据ID查询权限信息
     *
     * @param id 权限ID
     * @return 权限信息 VO
     */
    @Override
    @Cacheable(value = "sysPermission", key = "#id")
    public PermissionVO get(String id) {
        Permission permission = permissionMapper.selectById(id);
        if (ObjectUtils.isEmpty(permission)) {
            throw new OneselfException("权限不存在");
        }
        PermissionVO vo = new PermissionVO();
        BeanCopyUtils.copy(permission, vo);
        return vo;
    }

    /**
     * 更新权限信息
     *
     * @param id  权限ID
     * @param dto 权限信息 DTO，包含更新后的权限名称、权限编码、资源类型、资源路径等
     * @return 更新是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysPermission", key = "#id")
    public boolean update(String id, PermissionDTO dto) {
        Permission existingPermission = permissionMapper.selectById(id);
        if (ObjectUtils.isEmpty(existingPermission)) {
            throw new OneselfException("权限不存在");
        }

        Permission permission = Permission.builder().id(id).build();
        BeanCopyUtils.copy(dto, permission);

        // 检查权限编码是否重复（排除当前权限）
        if (!existingPermission.getPermCode().equals(dto.getPermCode())) {
            DuplicateCheckUtils.checkDuplicateMultiFields(
                    permission,
                    Permission::getId,
                    permissionMapper::selectCount,
                    "权限编码已存在",
                    DuplicateCheckUtils.FieldCondition.of(Permission::getPermCode, DuplicateCheckUtils.ConditionType.EQ)
            );
        }

        int update = permissionMapper.updateById(permission);
        if (update == 0) {
            throw new OneselfException("更新权限失败");
        }
        log.info("权限更新成功, ID: {}", id);
        return true;
    }

    /**
     * 批量删除权限（逻辑删除）
     *
     * @param ids 权限ID列表
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<String> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }

        // 检查权限是否存在
        List<Permission> permissions = permissionMapper.selectByIds(ids);
        if (permissions.size() != ids.size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }

        // 删除权限
        int deleteCount = permissionMapper.deleteByIds(ids);
        if (deleteCount == 0) {
            throw new OneselfException("删除权限失败");
        }

        // 删除角色权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getPermId, ids));

        // 事务提交后清理缓存（避免回滚导致数据不一致）
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Cache permissionCache = cacheManager.getCache("sysPermission");
                if (permissionCache != null) {
                    ids.forEach(permissionCache::evict);
                }
            }
        });

        log.info("批量删除权限成功, 删除数量: {}", deleteCount);
        return true;
    }

    /**
     * 批量更新权限状态
     *
     * @param ids    权限ID列表
     * @param status 权限状态（启用/禁用）
     * @return 状态更新是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(List<String> ids, StatusEnum status) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }

        // 检查权限是否存在
        List<Permission> permissions = permissionMapper.selectByIds(ids);
        if (permissions.size() != ids.size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }

        // 批量更新状态
        LambdaUpdateWrapper<Permission> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Permission::getId, ids).set(Permission::getStatus, status);

        int updateCount = permissionMapper.update(null, wrapper);
        if (updateCount == 0) {
            throw new OneselfException("更新权限状态失败");
        }

        // 事务提交后清理缓存（避免回滚导致数据不一致）
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Cache permissionCache = cacheManager.getCache("sysPermission");
                if (permissionCache != null) {
                    ids.forEach(permissionCache::evict);
                }
            }
        });

        log.info("批量更新权限状态成功, 更新数量: {}, 状态: {}", updateCount, status);
        return true;
    }

    /**
     * 分页查询权限列表
     *
     * @param dto 分页查询 DTO，包含页码、每页大小及查询条件
     * @return 分页结果 PageVO<PermissionVO>
     */
    @Override
    public PageResp<PermissionVO> page(PageReq<PermissionQueryDTO> dto) {
        // 构建查询条件
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();

        if (ObjectUtils.isNotEmpty(dto.getCondition())) {
            PermissionQueryDTO query = dto.getCondition();

            if (ObjectUtils.isNotEmpty(query.getPermCode())) {
                wrapper.like(Permission::getPermCode, query.getPermCode());
            }
            if (ObjectUtils.isNotEmpty(query.getPermName())) {
                wrapper.like(Permission::getPermName, query.getPermName());
            }
            if (ObjectUtils.isNotEmpty(query.getResourceType())) {
                wrapper.eq(Permission::getResourceType, query.getResourceType());
            }
            if (ObjectUtils.isNotEmpty(query.getStatus())) {
                wrapper.eq(Permission::getStatus, query.getStatus());
            }
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Permission::getCreateTime);

        PageReq.Pagination pagination = dto.getPagination();
        // 分页查询
        Page<Permission> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        Page<Permission> permissionPage = permissionMapper.selectPage(page, wrapper);

        MyBatisPageWrapper<Permission> pageWrapper = new MyBatisPageWrapper<>(permissionPage);

        return PageResp.convert(pageWrapper, s -> {
            PermissionVO vo = new PermissionVO();
            BeanCopyUtils.copy(s, vo);
            return vo;
        });

    }

    /**
     * 查询权限树
     * <p>
     * 构建权限层级结构（用于前端展示菜单/按钮/接口树）
     * </p>
     *
     * @return 权限树列表
     */
    @Override
    @Cacheable(value = "sysPermission", key = "'tree'")
    public List<PermissionTreeVO> tree() {
        // 查询所有权限
        List<Permission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getStatus, StatusEnum.NORMAL)
                        .orderByAsc(Permission::getSortOrder)
        );

        if (ObjectUtils.isEmpty(permissions)) {
            return List.of();
        }

        // 转换为TreeVO
        List<PermissionTreeVO> treeVOs = permissions.stream()
                .map(permission -> {
                    PermissionTreeVO vo = new PermissionTreeVO();
                    BeanCopyUtils.copy(permission, vo);
                    return vo;
                })
                .toList();

        // 构建id与TreeVO的映射
        Map<String, PermissionTreeVO> idToTreeVOMap = new HashMap<>();
        treeVOs.forEach(vo -> idToTreeVOMap.put(vo.getId(), vo));

        // 构建树结构
        List<PermissionTreeVO> rootNodes = new ArrayList<>();
        for (PermissionTreeVO vo : treeVOs) {
            if (ObjectUtils.isEmpty(vo.getParentId())) {
                // 没有父节点的为顶级节点
                rootNodes.add(vo);
            } else {
                // 找到父节点，并把当前节点加入父节点的children中
                PermissionTreeVO parent = idToTreeVOMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }

        return rootNodes;
    }

    /**
     * 根据角色ID查询权限列表
     * <p>
     * 获取某个角色下所有分配的权限
     * </p>
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    @Cacheable(value = "sysPermission", key = "'role:' + #roleId")
    public List<PermissionVO> listByRoleId(String roleId) {
        // 查询角色权限关联
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId)
        );

        if (ObjectUtils.isEmpty(rolePermissions)) {
            return List.of();
        }

        // 获取权限ID列表
        List<String> permIds = rolePermissions.stream()
                .map(RolePermission::getPermId)
                .collect(Collectors.toList());

        // 查询权限信息
        List<Permission> permissions = permissionMapper.selectByIds(permIds);

        // 转换为VO
        return permissions.stream()
                .map(permission -> {
                    PermissionVO vo = new PermissionVO();
                    BeanCopyUtils.copy(permission, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
