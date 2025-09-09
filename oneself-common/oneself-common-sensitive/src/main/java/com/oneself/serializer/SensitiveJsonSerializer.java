package com.oneself.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.oneself.annotation.Sensitive;
import com.oneself.model.enums.DesensitizedTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.serializer
 * className SensitiveJsonSerializer
 * description
 * version 1.0
 */
@Slf4j
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    // 脱敏类型（从@Sensitive注解中获取）
    private DesensitizedTypeEnum desensitizedType;

    /**
     * 核心序列化逻辑：根据脱敏类型处理字段值
     */
    @Override
    public void serialize(String originalValue, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 1. 先判断是否需要脱敏（管理员不脱敏）
        /*if (!desensitization()) {
            gen.writeString(originalValue);
            return;
        }*/

        // 2. 处理空值：避免空指针异常
        if (originalValue == null) {
            gen.writeNull();
            return;
        }

        // 3. 调用枚举的脱敏方法处理值（修复原逻辑调用错误）
        String desensitizedValue = desensitizedType.desensitize(originalValue);
        gen.writeString(desensitizedValue);
    }

    /**
     * 上下文初始化：从字段的@Sensitive注解中获取脱敏类型
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        // 1. 校验字段是否有@Sensitive注解，且字段类型是String
        if (property == null) {
            return prov.findNullValueSerializer(null);
        }
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (annotation == null || !Objects.equals(String.class, property.getType().getRawClass())) {
            return prov.findValueSerializer(property.getType(), property);
        }

        // 2. 从注解中获取脱敏类型，初始化当前序列化器
        SensitiveJsonSerializer serializer = new SensitiveJsonSerializer();
        serializer.desensitizedType = annotation.value();
        return serializer;
    }

    /**
     * 判断是否需要脱敏：管理员不脱敏，其他用户/异常场景默认脱敏
     */
    /*private boolean desensitization() {
        try {
            // 从安全上下文获取用户ID（需确保SecurityContextHolder中正确存储了用户信息）
            Long userId = SecurityContextHolder.getUserId();
            // 调用用户常量类判断是否为管理员（非管理员则需要脱敏）
            return !UserConstants.isAdmin(userId);
        } catch (Exception e) {
            // 异常场景（如未登录、上下文获取失败）默认脱敏，避免敏感信息泄露
            log.warn("获取用户信息异常，默认执行脱敏处理", e);
            return true;
        }
    }*/
}