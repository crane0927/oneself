package com.oneself.utils;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.utils
 * className ExcelExportUtils
 * description Excel 导出工具类 （支持大数据量流式导出）适用于JDK 17/21
 * version 1.0
 */
public class ExcelExportUtils {

    private ExcelExportUtils() {
        // 工具类，禁止实例化
    }

    // ====================== 单元格操作 ======================

    /**
     * 创建字符串类型单元格
     *
     * @param row         行对象
     * @param columnIndex 列索引，从0开始
     * @param value       单元格内容
     * @param style       单元格样式，可为null
     */
    public static void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        if (style != null) cell.setCellStyle(style);
    }

    /**
     * 创建数值类型单元格
     *
     * @param row         行对象
     * @param columnIndex 列索引，从0开始
     * @param value       数值内容
     * @param style       单元格样式，可为null
     */
    public static void createCell(Row row, int columnIndex, Number value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value.doubleValue());
        if (style != null) cell.setCellStyle(style);
    }

    // ====================== 样式操作 ======================

    /**
     * 创建表头样式
     *
     * @param workbook 工作簿
     * @return CellStyle
     */
    public static CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setAllBordersThin(style);
        return style;
    }

    /**
     * 创建数据单元格样式
     *
     * @param workbook 工作簿
     * @return CellStyle
     */
    public static CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        setAllBordersThin(style);
        return style;
    }

    /**
     * 创建日期类型单元格样式
     *
     * @param workbook 工作簿
     * @return CellStyle
     */
    public static CellStyle createDateCellStyle(Workbook workbook) {
        CellStyle style = createDataCellStyle(workbook);
        style.setDataFormat(workbook.getCreationHelper().createDataFormat()
                .getFormat("yyyy-MM-dd HH:mm:ss"));
        return style;
    }

    /**
     * 创建数字类型单元格样式
     *
     * @param workbook 工作簿
     * @return CellStyle
     */
    public static CellStyle createNumberCellStyle(Workbook workbook) {
        CellStyle style = createDataCellStyle(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    /**
     * 设置单元格四边框为细线
     *
     * @param style 样式对象
     */
    private static void setAllBordersThin(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    // ====================== Sheet操作 ======================

    /**
     * 创建表头行
     *
     * @param sheet       Sheet对象
     * @param headers     表头文本数组
     * @param headerStyle 表头样式
     * @return 创建的表头行对象
     */
    public static Row createHeaderRow(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], headerStyle);
        }
        return row;
    }

    /**
     * 合并单元格
     *
     * @param sheet    Sheet对象
     * @param firstRow 起始行索引
     * @param lastRow  结束行索引
     * @param firstCol 起始列索引
     * @param lastCol  结束列索引
     */
    public static void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * 自动调整列宽
     *
     * @param sheet       Sheet对象
     * @param columnCount 列数
     */
    public static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 3000));
        }
    }

    /**
     * 自动调整列宽（默认6列）
     *
     * @param sheet Sheet对象
     */
    public static void autoSizeColumns(Sheet sheet) {
        autoSizeColumns(sheet, 6);
    }

    // ====================== Workbook操作 ======================

    /**
     * 创建普通Excel工作簿（适合小数据量）
     *
     * @return Workbook实例（XSSFWorkbook）
     */
    public static Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    /**
     * 创建大数据量Excel工作簿（流式写入，适合百万行）
     *
     * @return Workbook实例（SXSSFWorkbook）
     */
    public static Workbook createBigDataWorkbook() {
        // 保留100行在内存中，其余写入临时文件
        return new SXSSFWorkbook(100);
    }

    // ====================== HTTP响应 ======================

    /**
     * 将Workbook安全写入HttpServletResponse
     *
     * @param workbook 工作簿对象
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    public static void writeToResponse(Workbook workbook, HttpServletResponse response) throws IOException {
        try (workbook) {
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        }
    }

    /**
     * 设置Excel下载响应头
     *
     * @param response HttpServletResponse
     * @param fileName 文件名（包含扩展名）
     * @throws IOException IO异常
     */
    public static void setExcelResponseHeaders(HttpServletResponse response, String fileName) throws IOException {
        String encodedFileName = encodeFileName(fileName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setHeader(HttpHeaders.EXPIRES, "0");
        response.setHeader("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION);
    }

    /**
     * 设置Excel下载响应头（带时间戳和扩展名）
     *
     * @param response      HttpServletResponse
     * @param baseFileName  文件基础名
     * @param fileExtension 扩展名，例如.xlsx
     * @throws IOException IO异常
     */
    public static void setExcelResponseHeadersWithTimestamp(HttpServletResponse response, String baseFileName, String fileExtension) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        setExcelResponseHeaders(response, baseFileName + "_" + timestamp + fileExtension);
    }

    /**
     * 设置Excel下载响应头（带时间戳，默认.xlsx）
     *
     * @param response     HttpServletResponse
     * @param baseFileName 文件基础名
     * @throws IOException IO异常
     */
    public static void setExcelResponseHeadersWithTimestamp(HttpServletResponse response, String baseFileName) throws IOException {
        setExcelResponseHeadersWithTimestamp(response, baseFileName, ".xlsx");
    }

    /**
     * 设置大文件下载响应头（支持断点续传）
     *
     * @param response HttpServletResponse
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @throws IOException IO异常
     */
    public static void setLargeFileResponseHeaders(HttpServletResponse response, String fileName, long fileSize) throws IOException {
        setExcelResponseHeaders(response, fileName);
        if (fileSize > 0) response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
        response.setHeader("Accept-Ranges", "bytes");
    }

    /**
     * 设置下载错误响应头
     *
     * @param response     HttpServletResponse
     * @param errorMessage 错误信息
     * @throws IOException IO异常
     */
    public static void setErrorResponseHeaders(HttpServletResponse response, String errorMessage) throws IOException {
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }

    /**
     * 编码文件名，处理中文及特殊字符
     *
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    private static String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } catch (Exception e) {
            return fileName;
        }
    }
}