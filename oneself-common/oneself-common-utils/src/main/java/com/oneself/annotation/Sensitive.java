package com.oneself.annotation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.annotation
 * interfaceName Sensitive
 * description 标记敏感字段
 * version 1.0
 */
@Target(ElementType.FIELD) // 用于字段
@Retention(RetentionPolicy.RUNTIME)
@JsonIgnore
public @interface Sensitive {
}
