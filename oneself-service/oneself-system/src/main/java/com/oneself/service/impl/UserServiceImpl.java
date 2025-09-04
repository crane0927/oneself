package com.oneself.service.impl;

import com.oneself.exception.OneselfException;
import com.oneself.mapper.DeptMapper;
import com.oneself.mapper.UserMapper;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.QueryDTO;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.pojo.User;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.UserVO;
import com.oneself.service.UserService;
import com.oneself.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
    private final DeptMapper deptMapper;

    @Override
    public String add(UserDTO dto) {
        return null;
    }

    @Override
    public UserVO get(String id) {
        User user = userMapper.selectById(id);
        if (ObjectUtils.isEmpty(user)) {
            throw new OneselfException("用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanCopyUtils.copy(user, userVO);
        return userVO;
    }

    @Override
    public boolean update(String id, UserDTO dto) {
        return false;
    }

    @Override
    public boolean delete(List<String> ids) {
        return false;
    }

    @Override
    public boolean updateStatus(List<String> ids, StatusEnum status) {
        return false;
    }

    @Override
    public PageVO<UserVO> page(PageDTO<QueryDTO> dto) {
        return null;
    }



}
