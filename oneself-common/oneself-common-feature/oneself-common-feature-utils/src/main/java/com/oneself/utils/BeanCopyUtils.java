package com.oneself.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuhuan
 * date 2025/4/29
 * packageName com.oneself.utils
 * className BeanCopyUtils
 * description Bean 拷贝工具类
 * version 1.0
 */
public class BeanCopyUtils {

    private static final Map<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();

    /**
     * 复制对象属性，不忽略null
     */
    public static <S, T> T copy(S source, Class<T> targetClass) {
        if (source == null) return null;
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copy(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("BeanCopyUtils copy error", e);
        }
    }

    /**
     * 复制属性到已存在对象，不忽略null
     */
    public static <S, T> void copy(S source, T target) {
        if (source == null || target == null) return;
        String key = generateKey(source.getClass(), target.getClass(), false);
        BeanCopier copier = BEAN_COPIER_CACHE.computeIfAbsent(key, k ->
                BeanCopier.create(source.getClass(), target.getClass(), false));
        copier.copy(source, target, null);
    }

    /**
     * 复制对象属性，忽略source中为null的字段
     */
    public static <S, T> T copyIgnoreNull(S source, Class<T> targetClass) {
        if (source == null) return null;
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            copyIgnoreNull(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("BeanCopyUtils copyIgnoreNull error", e);
        }
    }

    /**
     * 复制属性到已存在对象，忽略source中为null的字段
     */
    public static <S, T> void copyIgnoreNull(S source, T target) {
        if (source == null || target == null) return;
        String key = generateKey(source.getClass(), target.getClass(), true);
        BeanCopier copier = BEAN_COPIER_CACHE.computeIfAbsent(key, k ->
                BeanCopier.create(source.getClass(), target.getClass(), true));
        copier.copy(source, target, new IgnoreNullConverter(source));
    }

    private static String generateKey(Class<?> sourceClass, Class<?> targetClass, boolean useConverter) {
        return sourceClass.getName() + "->" + targetClass.getName() + ":" + useConverter;
    }

    private static class IgnoreNullConverter implements Converter {
        private final BeanWrapper srcWrapper;

        public IgnoreNullConverter(Object source) {
            this.srcWrapper = new BeanWrapperImpl(source);
        }

        @Override
        public Object convert(Object value, Class target, Object setterName) {
            String propertyName = setterName.toString();
            Object srcValue = srcWrapper.getPropertyValue(propertyName);
            return srcValue != null ? value : null;
        }
    }
}

