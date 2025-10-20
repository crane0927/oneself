package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.oneself.pagination.MyBatisPageWrapper;
import com.oneself.utils.BeanCopyUtils;
import com.oneself.utils.DuplicateCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    private final CacheManager cacheManager;


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
    @Cacheable(value = "sysUser", key = "#id")
    public UserVO get(String id) {
        User user = Optional.ofNullable(
                userMapper.selectById(id)
        ).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * 查询登录用户
     *
     * @param name 用户名
     * @return 登录用户信息 VO
     */
    @Override
    @Cacheable(value = "sysUser", key = "'session:' + #name")
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
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysUser", key = "#id")
    public boolean update(String id, UserDTO dto) {
        User existingUser = userMapper.selectById(id);
        if (ObjectUtils.isEmpty(existingUser)) {
            throw new IllegalArgumentException("用户不存在");
        }

        User user = User.builder().build();
        BeanUtils.copyProperties(dto, user);
        user.setId(id);

        // 检查用户名是否重复（排除当前用户）
        if (!existingUser.getUsername().equals(dto.getUsername())) {
            DuplicateCheckUtils.checkDuplicateMultiFields(
                    user,
                    User::getId,
                    userMapper::selectCount,
                    "用户名已存在",
                    DuplicateCheckUtils.FieldCondition.of(User::getUsername, DuplicateCheckUtils.ConditionType.EQ)
            );
        }

        // 检查邮箱是否重复（排除当前用户）
        if (!ObjectUtils.isEmpty(dto.getEmail()) && !dto.getEmail().equals(existingUser.getEmail())) {
            DuplicateCheckUtils.checkDuplicateMultiFields(
                    user,
                    User::getId,
                    userMapper::selectCount,
                    "邮箱已存在",
                    DuplicateCheckUtils.FieldCondition.of(User::getEmail, DuplicateCheckUtils.ConditionType.EQ)
            );
        }

        // 检查手机号是否重复（排除当前用户）
        if (!ObjectUtils.isEmpty(dto.getPhone()) && !dto.getPhone().equals(existingUser.getPhone())) {
            DuplicateCheckUtils.checkDuplicateMultiFields(
                    user,
                    User::getId,
                    userMapper::selectCount,
                    "手机号码已存在",
                    DuplicateCheckUtils.FieldCondition.of(User::getPhone, DuplicateCheckUtils.ConditionType.EQ)
            );
        }

        int update = userMapper.updateById(user);
        if (update == 0) {
            throw new OneselfException("更新用户失败");
        }
        log.info("用户更新成功, ID: {}", id);
        return true;
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<String> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        // 检查用户是否存在
        List<User> users = userMapper.selectByIds(ids);
        if (users.size() != ids.size()) {
            throw new IllegalArgumentException("部分用户不存在");
        }

        // 逻辑删除用户
        int deleteCount = userMapper.deleteByIds(ids);
        if (deleteCount == 0) {
            throw new OneselfException("删除用户失败");
        }

        // 删除用户角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, ids));

        // 事务提交后清理缓存（避免回滚导致数据不一致）
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Cache userCache = cacheManager.getCache("sysUser");
                if (userCache != null) {
                    ids.forEach(userCache::evict);
                }
            }
        });

        log.info("批量删除用户成功, 删除数量: {}", deleteCount);
        return true;
    }

    /**
     * 批量更新用户状态
     *
     * @param ids    用户ID列表
     * @param status 用户状态
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(List<String> ids, StatusEnum status) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        // 检查用户是否存在
        List<User> users = userMapper.selectByIds(ids);
        if (users.size() != ids.size()) {
            throw new IllegalArgumentException("部分用户不存在");
        }

        // 批量更新状态
        User updateUser = new User();
        updateUser.setStatus(status);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getId, ids);

        int updateCount = userMapper.update(updateUser, wrapper);
        if (updateCount == 0) {
            throw new OneselfException("更新用户状态失败");
        }

        // 事务提交后清理缓存（避免回滚导致数据不一致）
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Cache userCache = cacheManager.getCache("sysUser");
                if (userCache != null) {
                    ids.forEach(userCache::evict);
                }
            }
        });

        log.info("批量更新用户状态成功, 更新数量: {}, 状态: {}", updateCount, status);
        return true;
    }

    /**
     * 分页查询用户
     *
     * @param dto 分页查询 DTO
     * @return 分页结果
     */
    @Override
    public PageVO<UserVO> page(PageDTO<UserQueryDTO> dto) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (ObjectUtils.isNotEmpty(dto.getCondition())) {
            UserQueryDTO query = dto.getCondition();

            if (ObjectUtils.isNotEmpty(query.getUsername())) {
                wrapper.like(User::getUsername, query.getUsername());
            }
            if (ObjectUtils.isNotEmpty(query.getEmail())) {
                wrapper.like(User::getEmail, query.getEmail());
            }
            if (ObjectUtils.isNotEmpty(query.getPhone())) {
                wrapper.like(User::getPhone, query.getPhone());
            }
            if (ObjectUtils.isNotEmpty(query.getRealName())) {
                wrapper.like(User::getRealName, query.getRealName());
            }
            if (ObjectUtils.isNotEmpty(query.getDeptId())) {
                wrapper.eq(User::getDeptId, query.getDeptId());
            }
            if (ObjectUtils.isNotEmpty(query.getSex())) {
                wrapper.eq(User::getSex, query.getSex());
            }
            if (ObjectUtils.isNotEmpty(query.getType())) {
                wrapper.eq(User::getType, query.getType());
            }
            if (ObjectUtils.isNotEmpty(query.getStatus())) {
                wrapper.eq(User::getStatus, query.getStatus());
            }
        }

        // 按创建时间倒序
        wrapper.orderByDesc(User::getCreateTime);

        // 分页查询
        PageDTO.Pagination pagination = dto.getPagination();
        Page<User> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        // 对齐 DeptServiceImpl 的分页转换
        MyBatisPageWrapper<User> pageWrapper = new MyBatisPageWrapper<>(userPage);
        return PageVO.convert(pageWrapper, user -> {
            UserVO vo = new UserVO();
            BeanCopyUtils.copy(user, vo);
            return vo;
        });
    }

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    @Override
    @Cacheable(value = "sysUser", key = "'dept:' + #deptId")
    public List<UserVO> listByDeptId(String deptId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeptId, deptId);
        wrapper.orderByDesc(User::getCreateTime);

        List<User> users = userMapper.selectList(wrapper);

        return users.stream()
                .map(user -> {
                    UserVO vo = new UserVO();
                    BeanUtils.copyProperties(user, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
