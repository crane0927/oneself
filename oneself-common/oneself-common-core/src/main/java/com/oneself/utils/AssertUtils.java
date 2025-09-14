package com.oneself.utils;

import com.oneself.exception.OneselfException;
import org.springframework.lang.Contract;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.function.Supplier;


/**
 * @author liuhuan
 * date 2024/12/31
 * packageName com.oneself.utils
 * className AssertUtils
 * description 断言工具类
 * <p>断言工具类，用于条件校验，支持抛出默认异常 {@link OneselfException} 或自定义 {@link RuntimeException} 类型</p>
 * <p>提供常用断言方法，包括：</p>
 * <ul>
 *     <li>布尔条件判断：isTrue / isFalse</li>
 *     <li>对象非空 / 空：notNull / isNull</li>
 *     <li>字符串非空：hasText</li>
 *     <li>集合非空：notEmpty</li>
 * </ul>
 * <p>工具类不可实例化</p>
 */
public class AssertUtils {

    /**
     * 私有构造方法，防止实例化
     */
    private AssertUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    // ================= 默认抛 OneselfException =================

    /**
     * 断言对象不为 null，否则抛 {@link OneselfException}
     *
     * @param object 对象
     * @param message 异常信息
     */
    @Contract("null -> fail")
    public static void notNull(Object object, String message) {
        notNull(object, () -> new OneselfException(message));
    }

    /**
     * 断言字符串有内容，否则抛 {@link OneselfException}
     *
     * @param text 字符串
     * @param message 异常信息
     */
    @Contract("null -> fail")
    public static void hasText(String text, String message) {
        hasText(text, () -> new OneselfException(message));
    }

    /**
     * 断言集合不为空，否则抛 {@link OneselfException}
     *
     * @param collection 集合
     * @param message 异常信息
     */
    @Contract("null -> fail")
    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, () -> new OneselfException(message));
    }

    /**
     * 断言布尔表达式为 true，否则抛 {@link OneselfException}
     *
     * @param expression 条件表达式
     * @param message 异常信息
     */
    @Contract("false -> fail")
    public static void isTrue(boolean expression, String message) {
        isTrue(expression, () -> new OneselfException(message));
    }

    /**
     * 断言布尔表达式为 false，否则抛 {@link OneselfException}
     *
     * @param expression 条件表达式
     * @param message 异常信息
     */
    @Contract("true -> fail")
    public static void isFalse(boolean expression, String message) {
        isFalse(expression, () -> new OneselfException(message));
    }


    // ================= 支持 Supplier<RuntimeException> =================

    /**
     * 断言对象不为 null，否则抛由 {@link Supplier} 提供的异常
     *
     * @param object 对象
     * @param exceptionSupplier 异常供应器
     */
    @Contract("null -> fail")
    public static void notNull(Object object, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (object == null) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 断言字符串有内容，否则抛由 {@link Supplier} 提供的异常
     *
     * @param text 字符串
     * @param exceptionSupplier 异常供应器
     */
    @Contract("null -> fail")
    public static void hasText(String text, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!StringUtils.hasText(text)) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 断言集合不为空，否则抛由 {@link Supplier} 提供的异常
     *
     * @param collection 集合
     * @param exceptionSupplier 异常供应器
     */
    @Contract("null -> fail")
    public static void notEmpty(Collection<?> collection, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (collection == null || collection.isEmpty()) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 断言布尔表达式为 true，否则抛由 {@link Supplier} 提供的异常
     *
     * @param expression 条件表达式
     * @param exceptionSupplier 异常供应器
     */
    @Contract("false -> fail")
    public static void isTrue(boolean expression, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!expression) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 断言布尔表达式为 false，否则抛由 {@link Supplier} 提供的异常
     *
     * @param expression 条件表达式
     * @param exceptionSupplier 异常供应器
     */
    @Contract("true -> fail")
    public static void isFalse(boolean expression, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (expression) {
            throw exceptionSupplier.get();
        }
    }

}