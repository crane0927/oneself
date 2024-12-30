package com.oneself.utils;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.utils
 * className EnumUtils
 * description 枚举工具类
 * version 1.0
 */
public class EnumUtils {

    /**
     * 私有构造器，防止被实例化
     */
    private EnumUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    /**
     * 判断枚举类中是否包含指定的值
     *
     * @param enumClass 枚举类
     * @param value     值
     * @param <T>       枚举类型
     * @return true：包含，false：不包含
     */
    public static <T extends Enum<T>> boolean contains(Class<T> enumClass, String value) {
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
