package com.oneself.common.feature.sensitive.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.oneself.common.feature.sensitive.annotation.Sensitive;
import com.oneself.common.feature.sensitive.model.enums.DesensitizeSceneEnum;
import com.oneself.common.feature.sensitive.model.enums.DesensitizedTypeEnum;
import com.oneself.common.feature.sensitive.utils.UserPermissionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;

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

    /**
     * 存储当前字段的脱敏类型（通过 createContextual 方法初始化）
     */
    private DesensitizedTypeEnum currentDesensitizeType;
    /**
     * 存储当前字段的脱敏场景（日志/前端/两者）
     */
    private DesensitizeSceneEnum currentDesensitizeScene;

    /**
     * 核心序列化逻辑：按注解规则和用户权限脱敏
     */
    @Override
    public void serialize(String originalValue, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 1. 先判断场景：仅处理“前端专用”或“两者都脱敏”的字段（跳过仅日志脱敏的字段）
        if (currentDesensitizeScene != DesensitizeSceneEnum.RESPONSE_ONLY
                && currentDesensitizeScene != DesensitizeSceneEnum.BOTH) {
            gen.writeString(originalValue);
            return;
        }

        // 2. 管理员不脱敏：直接返回原始值
        if (UserPermissionUtils.isAdmin()) {
            gen.writeString(originalValue);
            return;
        }

        // 3. 空值处理：避免空指针异常
        if (originalValue == null) {
            gen.writeNull();
            return;
        }

        // 4. 执行脱敏（脱敏类型为NONE时不处理）
        if (currentDesensitizeType == DesensitizedTypeEnum.NONE) {
            gen.writeString(originalValue);
            return;
        }

        try {
            String desensitizedValue = currentDesensitizeType.desensitize(originalValue);
            gen.writeString(desensitizedValue);
            log.debug("前端响应脱敏：原始值[{}] → 脱敏后[{}]", originalValue, desensitizedValue);
        } catch (Exception e) {
            // 脱敏失败时返回原始值（避免前端展示异常），同时记录日志
            log.warn("字段脱敏失败，返回原始值，异常原因：{}", e.getMessage(), e);
            gen.writeString(originalValue);
        }
    }

    /**
     * 上下文初始化：获取当前字段的@Sensitive注解配置（替代原代码中直接反射字段）
     * 作用：在序列化前初始化脱敏类型和场景，避免每次serialize都反射字段
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        // 1. 若字段未标注 @Sensitive，返回默认序列化器（不脱敏）
        if (property == null || property.getAnnotation(Sensitive.class) == null) {
            try {
                return prov.findNullValueSerializer(property);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            }
        }

        // 2. 从字段注解中获取脱敏配置
        Sensitive sensitiveAnno = property.getAnnotation(Sensitive.class);
        this.currentDesensitizeType = sensitiveAnno.value();
        this.currentDesensitizeScene = sensitiveAnno.scene();

        // 3. 返回初始化后的当前序列化器
        return this;
    }

    /**
     * 递归获取字段（包括父类字段）
     *
     * @param clazz     当前类
     * @param fieldName 字段名
     * @return 字段对象，未找到返回 null
     */
    private Field getFieldRecursive(Class<?> clazz, String fieldName) {
        // 递归终止条件：直到 Object 类仍未找到，返回 null
        if (clazz == Object.class) {
            return null;
        }

        try {
            // 先从当前类获取字段
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // 当前类未找到，递归查询父类
            return getFieldRecursive(clazz.getSuperclass(), fieldName);
        }
    }
}