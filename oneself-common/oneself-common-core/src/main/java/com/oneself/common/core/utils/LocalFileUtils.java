package com.oneself.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.utils
 * className LocalFileUtils
 * description  本地文件获取工具类
 * version 1.0
 */
@Slf4j
public class LocalFileUtils {

    private LocalFileUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    /**
     * 获取文件内容
     *
     * @param fileName 文件名
     * @param savePath 文件路径
     * @return 文件内容
     */
    public static String getLocalFile(String fileName, String savePath) {
        // 读取文件信息
        String jsonFilePath = savePath + File.separator + fileName;
        File file = new File(jsonFilePath);

        String input = null;
        try {
            input = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取文件失败 : {}", e.getMessage());
        }
        return input;
    }
}
