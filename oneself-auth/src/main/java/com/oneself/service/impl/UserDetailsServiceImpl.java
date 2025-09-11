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
        ResponseVO<LoginUserVO> vo = userClient.getLoginUserByName(username);
        LoginUserVO userVO = vo.getData();
        if (userVO == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new LoginUserBO(userVO);
    }

}
