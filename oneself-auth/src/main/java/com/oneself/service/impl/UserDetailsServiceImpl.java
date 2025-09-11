package com.oneself.service.impl;


import com.oneself.client.UserClient;
import com.oneself.model.bo.LoginUserBO;
import com.oneself.model.vo.LoginUserVO;
import com.oneself.model.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.service.impl
 * className AuthServiceImpl
 * description
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 调用远程服务获取用户信息
        ResponseVO<LoginUserVO> vo = userClient.getLoginUserByName(username);
        LoginUserVO userVO = vo.getData();

        // 用户不存在
        if (userVO == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 使用继承方式封装 LoginUserBO 返回给 Spring Security
        return new LoginUserBO(userVO);
    }

}
