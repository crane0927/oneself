package com.oneself.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuhuan
 * date 2025/9/14
 * packageName com.oneself.annotation
 * interfaceName RequirePermission
 * description   权限控制
 * version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String[] value(); // 权限码列表
    boolean strict() default true;
}
