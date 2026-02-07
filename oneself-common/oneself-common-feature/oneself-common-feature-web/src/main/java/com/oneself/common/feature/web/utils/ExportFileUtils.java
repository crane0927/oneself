package com.oneself.common.feature.web.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author liuhuan
 * date 2025/2/8
 * packageName com.oneself.utils
 * className ExportFileUtils
 * description 文件导出工具类
 * version 1.0
 */
public class ExportFileUtils {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment; filename=\"%s\"";
    private static final int MAX_ROWS_PER_SHEET = 100000; // 每个 Sheet 页的最大行数

    private ExportFileUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    /**
     * 导出数据到 Excel 文件，支持大数据量分 Sheet 页。
     *
     * @param response HttpServletResponse，用于写出文件流
     * @param fileName 导出的文件名
     * @param data     数据列表，每行数据为 Map 对象，键为列名
     * @param headers  列头映射，键为列标识，值为中文名称
     * @throws IOException 导出过程中的异常
     */
    public static void exportToExcel(HttpServletResponse response, String fileName, List<Map<String, Object>> data, Map<String, String> headers) throws IOException {
        // 使用 SXSSFWorkbook 以流式写入支持大数据量导出
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(); OutputStream outputStream = response.getOutputStream()) {
            int totalRows = data.size(); // 总数据行数
            int sheetCount = (int) Math.ceil((double) totalRows / MAX_ROWS_PER_SHEET); // 计算需要的 Sheet 页数量

            // 为每个 Sheet 页写入数据
            for (int i = 0; i < sheetCount; i++) {
                String sheetName = "Sheet" + (i + 1); // 动态生成 Sheet 页名称
                Sheet sheet = workbook.createSheet(sheetName);
                createHeaderRow(sheet, headers); // 创建标题行

                // 根据表头中文名称设置每列宽度
                int columnIndex = 0;
                for (String header : headers.values()) {
                    sheet.setColumnWidth(columnIndex, (header.length() + 4) * 512); // 设置列宽，增加额外空隙
                    columnIndex++;
                }

                // 计算当前 Sheet 页的数据范围
                int startRow = i * MAX_ROWS_PER_SHEET;
                int endRow = Math.min(startRow + MAX_ROWS_PER_SHEET, totalRows);
                for (int rowIndex = startRow; rowIndex < endRow; rowIndex++) {
                    Row row = sheet.createRow(rowIndex - startRow + 1); // 数据从第 2 行开始写入
                    Map<String, Object> rowData = data.get(rowIndex);
                    int cellIndex = 0;

                    // 填充当前行数据
                    for (String key : headers.keySet()) {
                        Cell cell = row.createCell(cellIndex++);
                        cell.setCellValue(rowData.getOrDefault(key, "").toString()); // 防止空值
                    }
                }
            }

            // 设置响应头，写出 Excel 文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_ATTACHMENT, fileName));
            workbook.write(outputStream);
        }
    }

    /**
     * 创建标题行。
     *
     * @param sheet   工作表对象
     * @param headers 标题映射，键为列标识，值为中文名称
     */
    private static void createHeaderRow(Sheet sheet, Map<String, String> headers) {
        Row headerRow = sheet.createRow(0); // 标题行固定在第一行
        int columnIndex = 0;
        for (String header : headers.values()) {
            Cell cell = headerRow.createCell(columnIndex++);
            cell.setCellValue(header); // 设置标题文字
            cell.setCellStyle(createHeaderCellStyle(sheet.getWorkbook())); // 应用标题样式
        }
    }

    /**
     * 创建标题单元格样式。
     *
     * @param workbook 工作簿对象
     * @return 样式对象
     */
    private static CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true); // 标题加粗
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER); // 居中对齐
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        return style;
    }


    /**
     * 导出数据到 ZIP 文件。
     *
     * @param response HttpServletResponse，用于写出文件流
     * @param fileName 导出的 ZIP 文件名
     * @param files    待压缩的文件集合，键为文件名，值为文件内容
     * @throws IOException 导出过程中的异常
     */
    public static void exportToZip(HttpServletResponse response, String fileName, Map<String, byte[]> files) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            response.setContentType("application/zip");
            response.setHeader(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_ATTACHMENT, fileName));

            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(entry.getValue());
                zipOut.closeEntry();
            }
        }
    }

    /**
     * 导出数据到 TXT 文件。
     *
     * @param response HttpServletResponse，用于写出文件流
     * @param fileName 导出的 TXT 文件名
     * @param content  文本内容
     * @throws IOException 导出过程中的异常
     */
    public static void exportToTxt(HttpServletResponse response, String fileName, String content) throws IOException {
        response.setContentType("text/plain");
        response.setHeader(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_ATTACHMENT, fileName));
        try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream())) {
            writer.write(content);
        }
    }

    /**
     * 导出数据到 JSON 文件。
     *
     * @param response HttpServletResponse，用于写出文件流
     * @param fileName 导出的 JSON 文件名
     * @param data     数据对象
     * @throws IOException 导出过程中的异常
     */
    public static void exportToJson(HttpServletResponse response, String fileName, Object data) throws IOException {
        response.setContentType("application/json");
        response.setHeader(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_ATTACHMENT, fileName));
        ObjectMapper objectMapper = new ObjectMapper();
        try (OutputStream outputStream = response.getOutputStream()) {
            objectMapper.writeValue(outputStream, data);
        }
    }

    /**
     * 导出数据到 Word 文件。
     *
     * @param response HttpServletResponse，用于写出文件流
     * @param fileName 导出的 Word 文件名
     * @param content  文档内容（纯文本格式）
     * @throws IOException 导出过程中的异常
     */
    public static void exportToWord(HttpServletResponse response, String fileName, String content) throws IOException {
        response.setContentType("application/msword");
        response.setHeader(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_ATTACHMENT, fileName));
        try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream())) {
            writer.write(content);
        }
    }

}
