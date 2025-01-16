package com.oneself.annotation;

import java.lang.annotation.*;

/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.annotation
 * interfaceName LogRequestDetails
 * description 日志记录注解
 * version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface LogRequestDetails {
}
