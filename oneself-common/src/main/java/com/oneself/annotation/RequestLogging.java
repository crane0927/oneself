package com.oneself.annotation;

import java.lang.annotation.*;

/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.annotation
 * interfaceName RequestLogging
 * description 用于记录请求参数与返回结果
 * version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface RequestLogging {
}
