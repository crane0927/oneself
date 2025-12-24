package com.oneself.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oneself.model.enums.DesensitizeSceneEnum;
import com.oneself.model.enums.DesensitizedTypeEnum;
import com.oneself.serializer.SensitiveJsonSerializer;

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
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveJsonSerializer.class)
public @interface Sensitive {
    DesensitizedTypeEnum value() default DesensitizedTypeEnum.NONE;

    DesensitizeSceneEnum scene() default DesensitizeSceneEnum.LOG_ONLY;
}

