package com.oneself.common.feature.security.provider;

import com.oneself.common.core.provider.CurrentUserProvider;
import com.oneself.common.feature.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author liuhuan
 * date 2025/1/22
 * packageName com.oneself.provider
 * className SecurityCurrentUserProvider
 * description CurrentUserProvider 接口的安全模块实现，用于解耦 common-jdbc 和 common-security 的依赖关系
 * version 1.0
 */
@Component
@RequiredArgsConstructor
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    private final SecurityUtils securityUtils;

    /**
     * 获取当前用户名
     * 如果当前没有登录用户，返回默认值 "system"
     *
     * @return 当前用户名，如果未登录则返回 "system"
     */
    @Override
    public String getCurrentUsername() {
        return securityUtils.getCurrentUsername();
    }
}

