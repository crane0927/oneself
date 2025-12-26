package com.oneself.utils;

import java.text.SimpleDateFormat;

/**
 * @author liuhuan
 * date 2025/1/3
 * packageName com.oneself.utils
 * className DateFormatUtils
 * description 日期格式工具类
 * version 1.0
 */
public class DateFormatUtils {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    // 使用 ThreadLocal 确保每个线程都有独立的 SimpleDateFormat 实例
    private static final ThreadLocal<SimpleDateFormat> SDF_YYYY_MM_DD = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYY_MM_DD));
    private static final ThreadLocal<SimpleDateFormat> SDF_YYYY_MM_DD_HH = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYY_MM_DD_HH));
    private static final ThreadLocal<SimpleDateFormat> SDF_YYYY_MM_DD_HH_MM = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYY_MM_DD_HH_MM));
    private static final ThreadLocal<SimpleDateFormat> SDF_YYYY_MM_DD_HH_MM_SS = ThreadLocal.withInitial(() -> new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS));


    private DateFormatUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    public static SimpleDateFormat getSdfYyyyMmDd() {
        return SDF_YYYY_MM_DD.get();
    }

    public static SimpleDateFormat getSdfYyyyMmDdHh() {
        return SDF_YYYY_MM_DD_HH.get();
    }

    public static SimpleDateFormat getSdfYyyyMmDdHhMm() {
        return SDF_YYYY_MM_DD_HH_MM.get();
    }

    public static SimpleDateFormat getSdfYyyyMmDdHhMmSs() {
        return SDF_YYYY_MM_DD_HH_MM_SS.get();
    }

    /**
     * 清除当前线程的 ThreadLocal 变量，避免内存泄漏
     */
    public static void remove() {
        SDF_YYYY_MM_DD.remove();
        SDF_YYYY_MM_DD_HH.remove();
        SDF_YYYY_MM_DD_HH_MM.remove();
        SDF_YYYY_MM_DD_HH_MM_SS.remove();
    }
}
