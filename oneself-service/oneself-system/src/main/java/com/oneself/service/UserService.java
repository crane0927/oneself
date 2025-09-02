package com.oneself.service;

import com.oneself.model.dto.UserDTO;
import com.oneself.model.vo.UserVO;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.service
 * interfaceName UserService
 * description 用户接口
 * version 1.0
 */
public interface UserService {
    Integer add(UserDTO dto);

    UserVO get(Long id);
}
