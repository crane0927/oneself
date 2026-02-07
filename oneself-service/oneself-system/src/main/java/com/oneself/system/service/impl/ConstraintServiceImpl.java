package com.oneself.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.common.core.exception.OneselfException;
import com.oneself.system.mapper.ConstraintMapper;
import com.oneself.system.mapper.UserRoleMapper;
import com.oneself.system.model.dto.ConstraintDTO;
import com.oneself.system.model.dto.ConstraintQueryDTO;
import com.oneself.common.feature.security.model.enums.ConstraintTypeEnum;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.system.model.pojo.Constraint;
import com.oneself.system.model.vo.ConstraintVO;
import com.oneself.common.infra.jdbc.pagination.MyBatisPageWrapper;
import com.oneself.common.feature.web.req.PageReq;
import com.oneself.common.feature.web.resp.PageResp;
import com.oneself.system.service.ConstraintService;
import com.oneself.system.model.pojo.UserRole;
import com.oneself.common.core.utils.BeanCopyUtils;
import com.oneself.common.core.utils.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.service.impl
 * className ConstraintServiceImpl
 * description 约束配置服务实现类（RBAC2）
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ConstraintServiceImpl implements ConstraintService {

    private final ConstraintMapper constraintMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(ConstraintDTO dto) {
        Constraint constraint = Constraint.builder().build();
        BeanCopyUtils.copy(dto, constraint);

        int insert = constraintMapper.insert(constraint);
        if (insert == 0) {
            throw new OneselfException("新增约束配置失败");
        }
        log.info("约束配置添加成功, ID: {}", constraint.getId());
        return constraint.getId();
    }

    @Override
    @Cacheable(value = "sysConstraint", key = "#id")
    public ConstraintVO get(String id) {
        Constraint constraint = constraintMapper.selectById(id);
        if (ObjectUtils.isEmpty(constraint)) {
            throw new OneselfException("约束配置不存在");
        }
        ConstraintVO vo = new ConstraintVO();
        BeanCopyUtils.copy(constraint, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysConstraint", key = "#id")
    public boolean update(String id, ConstraintDTO dto) {
        Constraint existing = constraintMapper.selectById(id);
        if (ObjectUtils.isEmpty(existing)) {
            throw new OneselfException("约束配置不存在");
        }

        Constraint constraint = Constraint.builder().id(id).build();
        BeanCopyUtils.copy(dto, constraint);

        int update = constraintMapper.updateById(constraint);
        if (update == 0) {
            throw new OneselfException("更新约束配置失败");
        }
        log.info("约束配置更新成功, ID: {}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<String> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("约束配置ID列表不能为空");
        }

        int deleteCount = constraintMapper.deleteByIds(ids);
        if (deleteCount == 0) {
            throw new OneselfException("删除约束配置失败");
        }
        log.info("批量删除约束配置成功, 删除数量: {}", deleteCount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(List<String> ids, StatusEnum status) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("约束配置ID列表不能为空");
        }

        LambdaUpdateWrapper<Constraint> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Constraint::getId, ids).set(Constraint::getStatus, status);

        int updateCount = constraintMapper.update(null, wrapper);
        if (updateCount == 0) {
            throw new OneselfException("更新约束配置状态失败");
        }
        log.info("批量更新约束配置状态成功, 更新数量: {}, 状态: {}", updateCount, status);
        return true;
    }

    @Override
    public PageResp<ConstraintVO> page(PageReq<ConstraintQueryDTO> dto) {
        LambdaQueryWrapper<Constraint> wrapper = new LambdaQueryWrapper<>();

        if (ObjectUtils.isNotEmpty(dto.getCondition())) {
            ConstraintQueryDTO query = dto.getCondition();

            if (ObjectUtils.isNotEmpty(query.getConstraintType())) {
                wrapper.eq(Constraint::getConstraintType, query.getConstraintType());
            }
            if (ObjectUtils.isNotEmpty(query.getConstraintName())) {
                wrapper.like(Constraint::getConstraintName, query.getConstraintName());
            }
            if (ObjectUtils.isNotEmpty(query.getStatus())) {
                wrapper.eq(Constraint::getStatus, query.getStatus());
            }
        }

        wrapper.orderByDesc(Constraint::getCreateTime);

        PageReq.Pagination pagination = dto.getPagination();
        Page<Constraint> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        Page<Constraint> constraintPage = constraintMapper.selectPage(page, wrapper);

        MyBatisPageWrapper<Constraint> pageWrapper = new MyBatisPageWrapper<>(constraintPage);
        return PageResp.convert(pageWrapper, constraint -> {
            ConstraintVO vo = new ConstraintVO();
            BeanCopyUtils.copy(constraint, vo);
            return vo;
        });
    }

    @Override
    public void checkRoleMutexConstraint(List<String> roleIds) {
        // 查询所有启用的角色互斥约束
        List<Constraint> constraints = constraintMapper.selectList(
                new LambdaQueryWrapper<Constraint>()
                        .eq(Constraint::getConstraintType, ConstraintTypeEnum.ROLE_MUTEX)
                        .eq(Constraint::getStatus, StatusEnum.NORMAL)
        );

        if (ObjectUtils.isEmpty(constraints)) {
            return;
        }

        Set<String> roleIdSet = new HashSet<>(roleIds);

        for (Constraint constraint : constraints) {
            try {
                // 解析约束值：{"mutexRoles": ["roleId1", "roleId2"]}
                Map<String, Object> constraintValue = JacksonUtils.fromJson(
                        constraint.getConstraintValue(),
                        Map.class
                );

                @SuppressWarnings("unchecked")
                List<String> mutexRoles = (List<String>) constraintValue.get("mutexRoles");

                if (ObjectUtils.isNotEmpty(mutexRoles)) {
                    // 检查是否有互斥的角色同时存在
                    long count = mutexRoles.stream().filter(roleIdSet::contains).count();
                    if (count > 1) {
                        throw new OneselfException(
                                String.format("违反角色互斥约束[%s]：角色 %s 不能同时分配给同一用户",
                                        constraint.getConstraintName(), mutexRoles)
                        );
                    }
                }
            } catch (Exception e) {
                log.warn("解析角色互斥约束失败, 约束ID: {}, 错误: {}", constraint.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void checkPermissionMutexConstraint(List<String> permIds) {
        // 查询所有启用的权限互斥约束
        List<Constraint> constraints = constraintMapper.selectList(
                new LambdaQueryWrapper<Constraint>()
                        .eq(Constraint::getConstraintType, ConstraintTypeEnum.PERM_MUTEX)
                        .eq(Constraint::getStatus, StatusEnum.NORMAL)
        );

        if (ObjectUtils.isEmpty(constraints)) {
            return;
        }

        Set<String> permIdSet = new HashSet<>(permIds);

        for (Constraint constraint : constraints) {
            try {
                // 解析约束值：{"mutexPerms": ["permId1", "permId2"]}
                Map<String, Object> constraintValue = JacksonUtils.fromJson(
                        constraint.getConstraintValue(),
                        Map.class
                );

                @SuppressWarnings("unchecked")
                List<String> mutexPerms = (List<String>) constraintValue.get("mutexPerms");

                if (ObjectUtils.isNotEmpty(mutexPerms)) {
                    // 检查是否有互斥的权限同时存在
                    long count = mutexPerms.stream().filter(permIdSet::contains).count();
                    if (count > 1) {
                        throw new OneselfException(
                                String.format("违反权限互斥约束[%s]：权限 %s 不能同时拥有",
                                        constraint.getConstraintName(), mutexPerms)
                        );
                    }
                }
            } catch (Exception e) {
                log.warn("解析权限互斥约束失败, 约束ID: {}, 错误: {}", constraint.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void checkUserRoleCardinalityConstraint(String userId, List<String> roleIds) {
        // 查询所有启用的用户角色基数约束
        List<Constraint> constraints = constraintMapper.selectList(
                new LambdaQueryWrapper<Constraint>()
                        .eq(Constraint::getConstraintType, ConstraintTypeEnum.CARDINALITY)
                        .eq(Constraint::getStatus, StatusEnum.NORMAL)
        );

        if (ObjectUtils.isEmpty(constraints)) {
            return;
        }

        for (Constraint constraint : constraints) {
            try {
                // 解析约束值：{"type": "USER_ROLE", "max": 5}
                Map<String, Object> constraintValue = JacksonUtils.fromJson(
                        constraint.getConstraintValue(),
                        Map.class
                );

                String type = (String) constraintValue.get("type");
                if ("USER_ROLE".equals(type)) {
                    Integer max = (Integer) constraintValue.get("max");
                    if (max != null && roleIds.size() > max) {
                        throw new OneselfException(
                                String.format("违反用户角色基数约束[%s]：用户最多只能拥有 %d 个角色，当前尝试分配 %d 个",
                                        constraint.getConstraintName(), max, roleIds.size())
                        );
                    }
                }
            } catch (Exception e) {
                log.warn("解析用户角色基数约束失败, 约束ID: {}, 错误: {}", constraint.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void checkRolePermissionCardinalityConstraint(String roleId, List<String> permIds) {
        // 查询所有启用的角色权限基数约束
        List<Constraint> constraints = constraintMapper.selectList(
                new LambdaQueryWrapper<Constraint>()
                        .eq(Constraint::getConstraintType, ConstraintTypeEnum.CARDINALITY)
                        .eq(Constraint::getStatus, StatusEnum.NORMAL)
        );

        if (ObjectUtils.isEmpty(constraints)) {
            return;
        }

        for (Constraint constraint : constraints) {
            try {
                // 解析约束值：{"type": "ROLE_PERM", "max": 100}
                Map<String, Object> constraintValue = JacksonUtils.fromJson(
                        constraint.getConstraintValue(),
                        Map.class
                );

                String type = (String) constraintValue.get("type");
                if ("ROLE_PERM".equals(type)) {
                    Integer max = (Integer) constraintValue.get("max");
                    if (max != null && permIds.size() > max) {
                        throw new OneselfException(
                                String.format("违反角色权限基数约束[%s]：角色最多只能拥有 %d 个权限，当前尝试分配 %d 个",
                                        constraint.getConstraintName(), max, permIds.size())
                        );
                    }
                }
            } catch (Exception e) {
                log.warn("解析角色权限基数约束失败, 约束ID: {}, 错误: {}", constraint.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void checkRolePrerequisiteConstraint(String userId, List<String> roleIds) {
        // 查询用户当前已有的角色
        List<UserRole> existingUserRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        );
        Set<String> existingRoleIds = existingUserRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        // 合并现有角色和新分配的角色
        Set<String> allRoleIds = new HashSet<>(existingRoleIds);
        allRoleIds.addAll(roleIds);

        // 查询所有启用的先决条件约束
        List<Constraint> constraints = constraintMapper.selectList(
                new LambdaQueryWrapper<Constraint>()
                        .eq(Constraint::getConstraintType, ConstraintTypeEnum.PREREQUISITE)
                        .eq(Constraint::getStatus, StatusEnum.NORMAL)
        );

        if (ObjectUtils.isEmpty(constraints)) {
            return;
        }

        for (Constraint constraint : constraints) {
            try {
                // 解析约束值：{"targetRole": "roleId1", "prerequisiteRole": "roleId2"}
                Map<String, Object> constraintValue = JacksonUtils.fromJson(
                        constraint.getConstraintValue(),
                        Map.class
                );

                String targetRole = (String) constraintValue.get("targetRole");
                String prerequisiteRole = (String) constraintValue.get("prerequisiteRole");

                if (ObjectUtils.isNotEmpty(targetRole) && ObjectUtils.isNotEmpty(prerequisiteRole)) {
                    // 如果用户拥有目标角色，必须同时拥有先决条件角色
                    if (allRoleIds.contains(targetRole) && !allRoleIds.contains(prerequisiteRole)) {
                        throw new OneselfException(
                                String.format("违反角色先决条件约束[%s]：拥有角色 %s 必须先拥有角色 %s",
                                        constraint.getConstraintName(), targetRole, prerequisiteRole)
                        );
                    }
                }
            } catch (Exception e) {
                log.warn("解析角色先决条件约束失败, 约束ID: {}, 错误: {}", constraint.getId(), e.getMessage());
            }
        }
    }
}

