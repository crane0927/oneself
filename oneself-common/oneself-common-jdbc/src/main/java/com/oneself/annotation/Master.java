package com.oneself.annotation;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.lang.annotation.*;

/**
 * @author liuhuan
 * date 2025/8/28
 * packageName com.oneself.annotation
 * @interfaceName Master
 * description 主数据源
 * version 1.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DS("master")
public @interface Master {
}
