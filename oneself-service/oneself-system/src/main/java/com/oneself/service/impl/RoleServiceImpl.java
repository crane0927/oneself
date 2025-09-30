package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.exception.OneselfException;
import com.oneself.mapper.RoleMapper;
import com.oneself.mapper.UserRoleMapper;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.RoleDTO;
import com.oneself.model.dto.RoleQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.pojo.Role;
import com.oneself.model.pojo.UserRole;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.RoleVO;
import com.oneself.service.RoleService;
import com.oneself.pagination.MyBatisPageWrapper;
import com.oneself.utils.BeanCopyUtils;
import com.oneself.utils.DuplicateCheckUtils;
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
 * className RoleServiceImpl
 * description
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    /**
     * 新增角色
     *
     * @param dto 角色信息 DTO，包含角色名称、角色编码、角色描述等
     * @return 新增角色ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(RoleDTO dto) {
        Role role = Role.builder().build();
        BeanCopyUtils.copy(dto, role);

        // 检查角色编码是否重复
        DuplicateCheckUtils.checkDuplicateMultiFields(
                role,
                Role::getId,
                roleMapper::selectCount,
                "角色编码已存在",
                DuplicateCheckUtils.FieldCondition.of(Role::getRoleCode, DuplicateCheckUtils.ConditionType.EQ)
        );

        int insert = roleMapper.insert(role);
        if (insert == 0) {
            throw new OneselfException("新增角色失败");
        }
        log.info("角色添加成功, ID: {}", role.getId());
        return role.getId();
    }

    /**
     * 根据ID查询角色信息
     *
     * @param id 角色ID
     * @return 角色信息 VO
     */
    @Override
    public RoleVO get(String id) {
        Role role = roleMapper.selectById(id);
        if (ObjectUtils.isEmpty(role)) {
            throw new OneselfException("角色不存在");
        }
        RoleVO vo = new RoleVO();
        BeanCopyUtils.copy(role, vo);
        return vo;
    }

    /**
     * 更新角色信息
     *
     * @param id  角色ID
     * @param dto 角色信息 DTO，包含更新后的角色名称、角色编码、角色描述等
     * @return 更新是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(String id, RoleDTO dto) {
        Role existingRole = roleMapper.selectById(id);
        if (ObjectUtils.isEmpty(existingRole)) {
            throw new OneselfException("角色不存在");
        }

        Role role = Role.builder().id(id).build();
        BeanCopyUtils.copy(dto, role);

        // 检查角色编码是否重复（排除当前角色）
        if (!existingRole.getRoleCode().equals(dto.getRoleCode())) {
            DuplicateCheckUtils.checkDuplicateMultiFields(
                    role,
                    Role::getId,
                    roleMapper::selectCount,
                    "角色编码已存在",
                    DuplicateCheckUtils.FieldCondition.of(Role::getRoleCode, DuplicateCheckUtils.ConditionType.EQ)
            );
        }

        int update = roleMapper.updateById(role);
        if (update == 0) {
            throw new OneselfException("更新角色失败");
        }
        log.info("角色更新成功, ID: {}", id);
        return true;
    }

    /**
     * 批量删除角色（逻辑删除）
     *
     * @param ids 角色ID列表
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<String> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }

        // 检查角色是否存在
        List<Role> roles = roleMapper.selectByIds(ids);
        if (roles.size() != ids.size()) {
            throw new IllegalArgumentException("部分角色不存在");
        }

        // 删除角色
        int deleteCount = roleMapper.deleteByIds(ids);
        if (deleteCount == 0) {
            throw new OneselfException("删除角色失败");
        }

        // 删除用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().in(UserRole::getRoleId, ids));

        log.info("批量删除角色成功, 删除数量: {}", deleteCount);
        return true;
    }

    /**
     * 批量更新角色状态
     *
     * @param ids    角色ID列表
     * @param status 角色状态（启用/禁用）
     * @return 状态更新是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(List<String> ids, StatusEnum status) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }

        // 检查角色是否存在
        List<Role> roles = roleMapper.selectByIds(ids);
        if (roles.size() != ids.size()) {
            throw new IllegalArgumentException("部分角色不存在");
        }

        // 批量更新状态
        LambdaUpdateWrapper<Role> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Role::getId, ids).set(Role::getStatus, status);

        int updateCount = roleMapper.update(null, wrapper);
        if (updateCount == 0) {
            throw new OneselfException("更新角色状态失败");
        }

        log.info("批量更新角色状态成功, 更新数量: {}, 状态: {}", updateCount, status);
        return true;
    }

    /**
     * 分页查询角色列表
     *
     * @param dto 分页查询 DTO，包含页码、每页大小及查询条件
     * @return 分页结果 PageVO<RoleVO>
     */
    @Override
    public PageVO<RoleVO> page(PageDTO<RoleQueryDTO> dto) {
        // 构建查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        if (ObjectUtils.isNotEmpty(dto.getCondition())) {
            RoleQueryDTO query = dto.getCondition();

            if (ObjectUtils.isNotEmpty(query.getRoleCode())) {
                wrapper.like(Role::getRoleCode, query.getRoleCode());
            }
            if (ObjectUtils.isNotEmpty(query.getRoleName())) {
                wrapper.like(Role::getRoleName, query.getRoleName());
            }
            if (ObjectUtils.isNotEmpty(query.getStatus())) {
                wrapper.eq(Role::getStatus, query.getStatus());
            }
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Role::getCreateTime);

        // 分页查询
        PageDTO.Pagination pagination = dto.getPagination();
        Page<Role> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        Page<Role> rolePage = roleMapper.selectPage(page, wrapper);

        // 对齐 DeptServiceImpl 的分页转换
        MyBatisPageWrapper<Role> pageWrapper = new MyBatisPageWrapper<>(rolePage);
        return PageVO.convert(pageWrapper, role -> {
            RoleVO vo = new RoleVO();
            BeanCopyUtils.copy(role, vo);
            return vo;
        });
    }

    /**
     * 查询所有角色列表
     *
     * @return 所有角色信息列表
     */
    @Override
    public List<RoleVO> listAll() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, StatusEnum.NORMAL);
        wrapper.orderByDesc(Role::getCreateTime);

        List<Role> roles = roleMapper.selectList(wrapper);

        return roles.stream()
            .map(role -> {
                RoleVO vo = new RoleVO();
                BeanCopyUtils.copy(role, vo);
                return vo;
            })
            .collect(Collectors.toList());
    }
}
