package com.oneself.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.annotation.Sensitive;
import com.oneself.exception.OneselfException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

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

    private SensitiveDataUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

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
            // TODO 由于深拷贝过程中使用了 writeValueAsString 和 readValue，这会导致原始对象的类型信息和泛型信息在反序列化时丢失。
            // TODO 但是使用原始数据会导致返回给前端的数据发生改变
            String json = objectMapper.writeValueAsString(obj);
            Object copy = objectMapper.readValue(json, obj.getClass());
            maskSensitiveData(copy);
            return copy;

        } catch (Exception e) {
            log.error("在数据复制和屏蔽期间发生错误: {},{}", e, e.getMessage());
            throw new OneselfException("在数据复制和屏蔽期间发生错误", e);
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
            // 处理基本类型、包装类、字符串或枚举类型
            if (shouldSkip(obj)) {
                return;
            }

            // 处理集合类型
            if (obj instanceof Iterable) {
                handleIterable((Iterable<?>) obj);
                return;
            }

            // 处理普通对象的字段
            handleFields(obj);
        } catch (IllegalAccessException e) {
            log.error("屏蔽敏感数据错误: {},{}", e, e.getMessage());
            throw new OneselfException("屏蔽敏感数据错误", e);
        }
    }

    /**
     * 判断对象是否为基本类型、包装类、字符串或枚举类型
     */
    private static boolean shouldSkip(Object obj) {
        return obj instanceof String ||
                obj.getClass().isPrimitive() ||
                isWrapperType(obj.getClass()) ||
                obj.getClass().isEnum() ||
                obj instanceof LocalDateTime;
    }

    /**
     * 处理集合类型的数据
     */
    private static void handleIterable(Iterable<?> iterable) {
        for (Object element : iterable) {
            maskSensitiveData(element);
        }
    }

    /**
     * 处理普通对象的字段
     */
    private static void handleFields(Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true); // 设置可访问性
            } catch (Exception e) {
                continue;  // 跳过无法访问的字段
            }

            if (field.isAnnotationPresent(Sensitive.class)) {
                field.set(obj, "*"); // 屏蔽敏感字段
            } else {
                Object value = field.get(obj);
                if (value != null) {
                    maskSensitiveData(value);
                }
            }
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
