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
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat(YYYY_MM_DD);
    public static final SimpleDateFormat SDF_YYYY_MM_DD_HH = new SimpleDateFormat(YYYY_MM_DD_HH);
    public static final SimpleDateFormat SDF_YYYY_MM_DD_HH_MM = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
    public static final SimpleDateFormat SDF_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);


}
