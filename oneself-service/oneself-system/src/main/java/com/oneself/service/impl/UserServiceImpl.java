package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneself.exception.OneselfException;
import com.oneself.mapper.UserMapper;
import com.oneself.model.dto.LoginUserDTO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.dto.UserQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.pojo.User;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.UserVO;
import com.oneself.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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


    /**
     * 新增用户
     *
     * @param dto 用户信息 DTO
     * @return 新增用户ID
     */
    @Override
    public String add(UserDTO dto) {
        return "";
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
     * @param dto 登录用户 DTO
     * @return 用户信息 VO
     */
    @Override
    public UserVO getLoginUser(@Valid LoginUserDTO dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new OneselfException("用户名或密码不能为空");
        }
        UserVO userVO = new UserVO();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getPassword, password)
                .eq(User::getStatus, StatusEnum.NORMAL));
        if (user == null) {
            throw new OneselfException("用户名或密码错误");
        }
        BeanUtils.copyProperties(user, userVO);
        return userVO;
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
