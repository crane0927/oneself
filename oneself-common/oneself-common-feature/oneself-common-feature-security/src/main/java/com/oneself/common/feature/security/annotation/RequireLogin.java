package com.oneself.common.feature.security.annotation;


import java.lang.annotation.*;

/**
 * @author liuhuan
 * date 2024/12/4
 * packageName com.example.oneself.annotation
 * interfaceName RequireLogin
 * description 判断用户是否登录
 * version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface RequireLogin {
    boolean strict() default true;
}
