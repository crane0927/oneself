package com.oneself.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.annotation.Sensitive;
import com.oneself.exception.OneselfException;
import com.oneself.model.enums.DesensitizedTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.utils
 * className SensitiveDataUtils
 * description 敏感数据处理工具类（支持按 @Sensitive 注解指定的类型执行差异化脱敏）
 * version 1.0
 */
@Slf4j
public class SensitiveDataUtils {

    private SensitiveDataUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    private static final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();

    /**
     * 对象深拷贝并按 @Sensitive 注解规则脱敏敏感数据
     * 核心逻辑：先深拷贝避免修改原始对象，再对拷贝对象执行脱敏
     *
     * @param obj 要拷贝和脱敏的对象（支持普通对象、集合、嵌套对象）
     * @return 脱敏后的深拷贝对象
     */
    public static <T> T copyAndDesensitize(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            // 1. 深拷贝：通过 JSON 序列化-反序列化实现（兼容大部分场景，若有特殊类型需扩展 Jackson 配置）
            String json = objectMapper.writeValueAsString(obj);
            @SuppressWarnings("unchecked")
            T copyObj = (T) objectMapper.readValue(json, obj.getClass());

            // 2. 对拷贝对象执行脱敏（不影响原始对象）
            desensitizeObject(copyObj);
            return copyObj;

        } catch (Exception e) {
            log.error("对象深拷贝或脱敏失败，对象类型：{}，异常信息：{}", obj.getClass().getName(), e.getMessage(), e);
            throw new OneselfException("敏感数据处理失败", e);
        }
    }

    /**
     * 递归脱敏对象（支持普通对象、集合、嵌套对象）
     *
     * @param obj 待脱敏的对象
     */
    private static void desensitizeObject(Object obj) {
        if (obj == null) {
            return;
        }

        try {
            // 1. 跳过无需脱敏的基础类型（原有逻辑）
            if (shouldSkipDesensitize(obj)) {
                return;
            }

            // 新增：处理Map类型（仅脱敏value为自定义类的情况）
            if (obj instanceof Map) {
                handleMapDesensitize((Map<?, ?>) obj);
                return;
            }

            // 2. 处理集合类型（原有逻辑，补充自定义类判断）
            if (obj instanceof Iterable) {
                for (Object element : (Iterable<?>) obj) {
                    if (element != null && !isNonCustomClass(element.getClass())) {
                        desensitizeObject(element);
                    }
                }
                return;
            }

            // 3. 处理普通对象（仅自定义类）
            handleObjectFieldsDesensitize(obj);

        } catch (IllegalAccessException e) {
            log.error("对象脱敏失败：{}", obj.getClass().getName(), e);
            throw new OneselfException("敏感字段反射处理异常", e);
        }
    }

    /**
     * 处理 Map 类型：仅脱敏 value 为项目自定义类的情况（key 不脱敏，value 是自定义类则递归处理）
     */
    private static void handleMapDesensitize(Map<?, ?> map) throws IllegalAccessException {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            // 若 value 是自定义类，递归脱敏；key 和非自定义类 value 跳过
            if (value != null && !isNonCustomClass(value.getClass())) {
                desensitizeObject(value);
            }
        }
    }

    /**
     * 判断对象是否需要跳过脱敏（无需处理的基础类型）
     *
     * @param obj 待判断的对象
     * @return true=跳过脱敏，false=需要脱敏
     */
    private static boolean shouldSkipDesensitize(Object obj) {
        Class<?> clazz = obj.getClass();
        return obj instanceof String                  // 字符串本身不脱敏（由字段注解控制）
                || clazz.isPrimitive()                // 基本类型（int、long 等）
                || isWrapperType(clazz)               // 包装类型（Integer、Long 等）
                || clazz.isEnum()                     // 枚举类型
                || obj instanceof LocalDateTime;      // 时间类型（无需脱敏）
    }

    /**
     * 处理普通对象的字段脱敏（反射遍历+按注解规则脱敏）
     *
     * @param obj 待脱敏的普通对象
     */
    private static void handleObjectFieldsDesensitize(Object obj) throws IllegalAccessException {
        Class<?> objClass = obj.getClass();

        // 新增：跳过JDK内置类、第三方框架类，仅处理项目自定义类（com.oneself包下）
        if (isNonCustomClass(objClass)) {
            log.debug("跳过非项目自定义类的反射处理：{}", objClass.getName());
            return;
        }

        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            // 原有逻辑：过滤 static/final 字段
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
            } catch (SecurityException e) {
                log.warn("无法访问字段：{}，跳过脱敏", field.getName(), e);
                continue;
            }

            Object fieldValue = field.get(obj);
            // 情况 1：字段标注 @Sensitive → 执行脱敏
            if (field.isAnnotationPresent(Sensitive.class)) {
                if (!UserPermissionUtils.isAdmin()) {
                    Sensitive sensitiveAnno = field.getAnnotation(Sensitive.class);
                    DesensitizedTypeEnum desensitizeType = sensitiveAnno.value();
                    if (desensitizeType != DesensitizedTypeEnum.NONE && fieldValue != null) {
                        String originalValue = String.valueOf(fieldValue);
                        String desensitizedValue = desensitizeType.desensitize(originalValue);
                        field.set(obj, desensitizedValue);
                        log.debug("字段[{}]脱敏完成：{} → {}", field.getName(), originalValue, desensitizedValue);
                    }
                }
                continue;
            }

            // 情况2：未标注 @Sensitive → 递归处理嵌套对象（但先判断是否为自定义类）
            if (fieldValue != null && !isNonCustomClass(fieldValue.getClass())) {
                desensitizeObject(fieldValue);
            }
        }
    }

    /**
     * 判断是否为非项目自定义类（需跳过反射处理）
     * @param clazz 待判断的类
     * @return true=非自定义类（JDK/第三方框架类），false=项目自定义类
     */
    private static boolean isNonCustomClass(Class<?> clazz) {
        String className = clazz.getName();
        // 跳过 JDK 内置类（java.开头）、Jakarta类（javax.开头）、第三方框架类（如springframework、com.fasterxml.jackson）
        return className.startsWith("java.")
                || className.startsWith("javax.")
                || className.startsWith("jakarta.")
                || className.startsWith("org.springframework.")
                || className.startsWith("com.fasterxml.jackson.")
                || className.startsWith("org.apache."); // 可根据项目依赖补充其他第三方包前缀
    }

    /**
     * 判断是否为包装类（基础类型的包装对象）
     *
     * @param clazz 待判断的类
     * @return true=包装类，false=非包装类
     */
    private static boolean isWrapperType(Class<?> clazz) {
        return Objects.equals(clazz, Boolean.class) || Objects.equals(clazz, Integer.class)
                || Objects.equals(clazz, Character.class) || Objects.equals(clazz, Byte.class)
                || Objects.equals(clazz, Short.class) || Objects.equals(clazz, Double.class)
                || Objects.equals(clazz, Long.class) || Objects.equals(clazz, Float.class);
    }
}