package com.oneself.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneself.dept.mapper.DeptMapper;
import com.oneself.dept.model.pojo.Dept;
import com.oneself.exception.OneselfException;
import com.oneself.user.mapper.UserMapper;
import com.oneself.user.model.dto.UserDTO;
import com.oneself.user.model.pojo.User;
import com.oneself.user.model.vo.UserVO;
import com.oneself.user.service.UserService;
import com.oneself.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

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
    private final DeptMapper deptMapper;

    @Override
    public Integer add(UserDTO dto) {
        // 1. 构建用户对象
        User user = User.builder().build();
        BeanCopyUtils.copy(dto, user);
        // 2. 校验登录名是否重复
        checkLoginName(user);
        // 3. 校验部门是否存在
        Dept dept = deptMapper.selectById(user.getDeptId());
        if (ObjectUtils.isEmpty(dept)) {
            throw new OneselfException("部门不存在");
        }
        // 4. 插入用户
        return userMapper.insert(user);
    }

    @Override
    public UserVO get(Long id) {
        User user = userMapper.selectById(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new OneselfException("用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanCopyUtils.copy(user, userVO);
        return userVO;
    }

    /**
     * 校验登录名是否重复
     *
     * @param user 用户对象
     */
    void checkLoginName(User user) {
        Long id = user.getId();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(User::getId, id);
        }
        wrapper.eq(User::getLoginName, user.getLoginName());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new OneselfException("用户登录名已存在");
        }
    }
}
