package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneself.exception.OneselfException;
import com.oneself.mapper.*;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.dto.UserQueryDTO;
import com.oneself.model.enums.ConfigurationTypeEnum;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.pojo.*;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.UserSessionVO;
import com.oneself.model.vo.UserVO;
import com.oneself.service.UserService;
import com.oneself.utils.DuplicateCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.service.impl
 * className UserServiceImpl
 * description 用户接口实现类
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final ConfigurationMapper configurationMapper;


    /**
     * 新增用户
     *
     * @param dto 用户信息 DTO
     * @return 新增用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String add(UserDTO dto) {
        User user = User.builder().build();
        BeanUtils.copyProperties(dto, user);
        DuplicateCheckUtils.checkDuplicateMultiFields(
                user,
                User::getId,
                userMapper::selectCount,
                "用户名已存在",
                DuplicateCheckUtils.FieldCondition.of(User::getUsername, DuplicateCheckUtils.ConditionType.EQ)
        );
        DuplicateCheckUtils.checkDuplicateMultiFields(
                user,
                User::getId,
                userMapper::selectCount,
                "邮箱已存在",
                DuplicateCheckUtils.FieldCondition.of(User::getEmail, DuplicateCheckUtils.ConditionType.EQ)
        );
        DuplicateCheckUtils.checkDuplicateMultiFields(
                user,
                User::getId,
                userMapper::selectCount,
                "手机号码已存在",
                DuplicateCheckUtils.FieldCondition.of(User::getPhone, DuplicateCheckUtils.ConditionType.EQ)
        );

        Configuration configuration = configurationMapper.selectOne(new LambdaQueryWrapper<Configuration>()
                .eq(Configuration::getParamKey, "system:login:password")
                .eq(Configuration::getType, ConfigurationTypeEnum.SYSTEM));

        if (ObjectUtils.isEmpty(configuration)) {
            throw new OneselfException("系统参数配置不存在");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(configuration.getParamValue()));
        int insert = userMapper.insert(user);

        if (insert == 0) {
            throw new OneselfException("新增用户失败");
        }
        log.info("用户添加成功, ID: {}", user.getId());
        return user.getId();
    }

    /**
     * 根据ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息 VO
     */
    @Override
    public UserVO get(String id) {
        return null;
    }

    /**
     * 查询登录用户
     *
     * @param name 用户名
     * @return 登录用户信息 VO
     */
    @Override
    public UserSessionVO getSessionByName(String name) {
        User user = Optional.ofNullable(
                userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, name))
        ).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        UserSessionVO vo = new UserSessionVO();
        BeanUtils.copyProperties(user, vo);

        List<String> roleIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId,
                        user.getId()))
                .stream().map(UserRole::getRoleId).toList();
        if (!roleIds.isEmpty()) {
            List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIds));
            vo.setRoleCodes(roles.stream().map(Role::getRoleCode).collect(Collectors.toSet()));

            List<String> permissionIds = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>()
                            .in(RolePermission::getRoleId, roleIds))
                    .stream()
                    .map(RolePermission::getPermId).toList();
            if (!permissionIds.isEmpty()) {
                List<Permission> permissions = permissionMapper.selectList(new LambdaQueryWrapper<Permission>()
                        .in(Permission::getId, permissionIds));
                vo.setPermissionCodes(permissions.stream().map(Permission::getPermCode).collect(Collectors.toSet()));
            }
        }

        return vo;
    }


    /**
     * 更新用户信息
     *
     * @param id  用户ID
     * @param dto 用户信息 DTO
     * @return 更新是否成功
     */
    @Override
    public boolean update(String id, UserDTO dto) {
        return false;
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 删除是否成功
     */
    @Override
    public boolean delete(List<String> ids) {
        return false;
    }

    /**
     * 批量更新用户状态
     *
     * @param ids    用户ID列表
     * @param status 用户状态
     * @return 是否成功
     */
    @Override
    public boolean updateStatus(List<String> ids, StatusEnum status) {
        return false;
    }

    /**
     * 分页查询用户
     *
     * @param dto 分页查询 DTO
     * @return 分页结果
     */
    @Override
    public PageVO<UserVO> page(PageDTO<UserQueryDTO> dto) {
        return null;
    }

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    @Override
    public List<UserVO> listByDeptId(String deptId) {
        return List.of();
    }
}
