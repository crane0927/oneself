package com.oneself.provider;

/**
 * @author liuhuan
 * date 2025/1/22
 * packageName com.oneself.provider
 * interfaceName CurrentUserProvider
 * description 当前用户提供者接口，用于解耦 common-jdbc 和 common-security 的依赖关系
 * version 1.0
 */
public interface CurrentUserProvider {

    /**
     * 获取当前用户名
     * 如果当前没有登录用户，应返回默认值（如 "system"）
     *
     * @return 当前用户名，如果未登录则返回默认值
     */
    String getCurrentUsername();
}

