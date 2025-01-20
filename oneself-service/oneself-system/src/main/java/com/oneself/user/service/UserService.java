package com.oneself.user.service;

import com.oneself.user.model.dto.AddUserDTO;
import com.oneself.user.model.vo.UserVO;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.service
 * interfaceName UserService
 * description 用户接口
 * version 1.0
 */
public interface UserService {
    Integer addUser(AddUserDTO dto);

    UserVO getUser(Long id);
}