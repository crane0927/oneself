package com.oneself.utils;

import com.oneself.exception.OneselfException;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.Collection;

/**
 * @author liuhuan
 * date 2025/4/28
 * packageName com.oneself.utils
 * className AssertUtils
 * description 断言工具类，支持默认或指定异常类型
 * version 2.0
 */
public class AssertUtils {

    private AssertUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    // ================= 默认抛 OneselfException =================
    public static void isTrue(boolean expression, String message) {
        isTrue(expression, message, OneselfException.class);
    }

    public static void isFalse(boolean expression, String message) {
        isFalse(expression, message, OneselfException.class);
    }

    public static void notNull(Object object, String message) {
        notNull(object, message, OneselfException.class);
    }

    public static void isNull(Object object, String message) {
        isNull(object, message, OneselfException.class);
    }

    public static void hasText(String text, String message) {
        hasText(text, message, OneselfException.class);
    }

    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, message, OneselfException.class);
    }

    public static void isTrue(boolean expression, String message, Class<? extends RuntimeException> exceptionClass) {
        if (expression) {
            throw newException(exceptionClass, message);
        }
    }

    public static void isFalse(boolean expression, String message, Class<? extends RuntimeException> exceptionClass) {
        if (expression) {
            throw newException(exceptionClass, message);
        }
    }

    public static void notNull(Object object, String message, Class<? extends RuntimeException> exceptionClass) {
        if (object == null) {
            throw newException(exceptionClass, message);
        }
    }

    public static void isNull(Object object, String message, Class<? extends RuntimeException> exceptionClass) {
        if (object != null) {
            throw newException(exceptionClass, message);
        }
    }

    public static void hasText(String text, String message, Class<? extends RuntimeException> exceptionClass) {
        if (!StringUtils.hasText(text)) {
            throw newException(exceptionClass, message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message, Class<? extends RuntimeException> exceptionClass) {
        if (collection == null || collection.isEmpty()) {
            throw newException(exceptionClass, message);
        }
    }

    // ================= 辅助方法 =================
    private static RuntimeException newException(Class<? extends RuntimeException> clazz, String message) {
        try {
            Constructor<? extends RuntimeException> constructor = clazz.getConstructor(String.class);
            return constructor.newInstance(message);
        } catch (Exception e) {
            // 如果构造失败，兜底抛 OneselfException
            return new OneselfException(message, e);
        }
    }
}