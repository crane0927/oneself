package com.oneself.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.annotation.Sensitive;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.utils
 * className SensitiveDataUtils
 * description 敏感数据处理工具类
 * version 1.0
 */
@Slf4j
public class SensitiveDataUtils {

    private static final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();

    /**
     * 对象深拷贝并屏蔽敏感数据
     *
     * @param obj 要拷贝和处理的对象
     * @return 拷贝并屏蔽敏感数据后的对象
     */
    public static Object copyAndMaskSensitiveData(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            // 使用深拷贝保证不修改原始对象
            String json = objectMapper.writeValueAsString(obj);
            Object copy = objectMapper.readValue(json, obj.getClass());
            maskSensitiveData(copy);
            return copy;
        } catch (Exception e) {
            log.error("Error during data copy and masking: {},{}", e, e.getMessage());
            throw new RuntimeException("Error during data copy and masking", e);
        }
    }

    /**
     * 屏蔽敏感数据
     *
     * @param obj 要屏蔽数据的对象
     */
    private static void maskSensitiveData(Object obj) {
        if (obj == null) {
            return;
        }

        try {
            // 如果是基本类型、包装类或字符串，直接返回
            if (obj instanceof String || obj.getClass().isPrimitive() || isWrapperType(obj.getClass())) {
                return;
            }

            // 如果是集合，递归处理每个元素
            if (obj instanceof Iterable) {
                for (Object element : (Iterable<?>) obj) {
                    maskSensitiveData(element);
                }
                return;
            }

            // 如果是普通对象，反射检查每个字段
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                // TODO 对于范型处理有问题 java.lang.reflect.InaccessibleObjectException
                field.setAccessible(true);  // 尝试设置可访问
                if (field.isAnnotationPresent(Sensitive.class)) {
                    field.set(obj, "***"); // 屏蔽敏感字段
                } else {
                    Object value = field.get(obj);
                    maskSensitiveData(value); // 递归处理
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Error masking sensitive data: {},{}", e, e.getMessage());
            throw new RuntimeException("Error masking sensitive data", e);
        }
    }

    /**
     * 判断是否是包装类
     *
     * @param clazz 类
     * @return true 是包装类，false 不是包装类
     */
    private static boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(Integer.class) || clazz.equals(Character.class) ||
                clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(Double.class) ||
                clazz.equals(Long.class) || clazz.equals(Float.class);
    }
}
