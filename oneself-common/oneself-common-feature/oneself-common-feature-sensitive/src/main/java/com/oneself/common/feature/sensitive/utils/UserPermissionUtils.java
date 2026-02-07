package com.oneself.common.feature.sensitive.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.utils
 * className UserPermissionUtils
 * description 用户权限公共工具类，封装脱敏权限判断、管理员身份校验等通用逻辑，避免重复编码
 * version 1.0
 */
@Slf4j
public class UserPermissionUtils {


    private UserPermissionUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }


    /**
     * 判断当前用户是否为管理员身份
     * 核心规则：管理员无需脱敏（直接展示原始数据），普通用户/异常场景（未登录、上下文获取失败）默认需要脱敏
     *
     * @return true=无需脱敏（仅管理员），false=需要脱敏（普通用户/异常场景）
     */
    public static boolean isAdmin() {
        // TODO 此处先简单返回false，后续可集成 Spring Security 等框架完善权限判断逻辑
        /*try {
            // 1. 从Spring Security上下文获取当前用户ID（需确保上下文配置正确）
            Long currentUserId = SecurityContextHolder.getUserId();
            // 2. 管理员无需脱敏：直接返回UserConstants.isAdmin的结果（true=管理员，无需脱敏）
            return UserConstants.isAdmin(currentUserId);
        } catch (Exception e) {
            // 3. 异常场景（如未登录、上下文获取失败、用户ID格式错误）默认需要脱敏 → 返回false
            log.warn("获取当前用户权限信息异常，默认需要执行敏感数据脱敏", e);
            return false;
        }*/
        return false;
    }
}
