package com.oneself.common.feature.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuhuan
 * date 2025/9/14
 * packageName com.oneself.annotation
 * interfaceName RequireRoles
 * description  判断用户角色
 * version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRoles {
    String[] value(); // 角色编码列表

    boolean strict() default true;
}
