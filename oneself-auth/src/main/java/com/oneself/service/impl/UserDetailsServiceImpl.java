package com.oneself.service.impl;


import com.oneself.client.UserClient;
import com.oneself.model.bo.LoginUserBO;
import com.oneself.model.vo.UserVO;
import com.oneself.resp.Resp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Resp<UserVO> vo;
        try {
            vo = userClient.getUserByName(username);
        } catch (Exception e) {
            log.error("调用用户服务异常，username={}", username, e);
            throw new InternalAuthenticationServiceException("获取用户信息失败", e);
        }

        if (vo.getMsgCode() != HttpStatus.OK.value()) {
            throw new InternalAuthenticationServiceException("用户服务返回异常");
        }
        UserVO userVO = vo.getData();
        if (ObjectUtils.isEmpty(userVO)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        LoginUserBO bo = new LoginUserBO();
        BeanUtils.copyProperties(userVO, bo);
        return bo;
    }
}
