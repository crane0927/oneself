package com.oneself.utils;

import com.oneself.exception.OneselfException;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @author liuhuan
 * date 2025/4/28
 * packageName com.oneself.utils
 * className AssertUtils
 * description 断言工具类
 * version 1.0
 */
public class AssertUtils {

    private AssertUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }


    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new OneselfException(message);
        }
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new OneselfException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new OneselfException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new OneselfException(message);
        }
    }

    public static void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new OneselfException(message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new OneselfException(message);
        }
    }
}
