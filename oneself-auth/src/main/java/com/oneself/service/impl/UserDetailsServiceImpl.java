package com.oneself.service.impl;


import com.oneself.client.UserClient;
import com.oneself.model.bo.UserSessionBO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.model.vo.UserSessionVO;
import com.oneself.utils.AssertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
        AssertUtils.notEmpty(Collections.singleton(username), "用户名不能为空", UsernameNotFoundException.class);
        ResponseVO<UserSessionVO> vo;
        try {
            // 调用远程服务获取用户信息
            vo = userClient.getSessionByName(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("获取用户信息失败: " + e.getMessage(), e);
        }
        AssertUtils.notNull(vo, "获取用户信息异常", UsernameNotFoundException.class);
        UserSessionVO userVO = vo.getData();
        AssertUtils.notNull(userVO, "用户不存在", UsernameNotFoundException.class);

        // 使用继承方式封装 LoginUserBO 返回给 Spring Security
        return new UserSessionBO(userVO);
    }

}
