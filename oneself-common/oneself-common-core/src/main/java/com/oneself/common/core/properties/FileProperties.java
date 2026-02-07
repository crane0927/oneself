package com.oneself.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.properties
 * className FileProperties
 * description 文件服务配置
 * version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "oneself.file")
public class FileProperties {

    private SftpConfig sftp;
    private FtpConfig ftp;

    @Data
    public static class SftpConfig {
        private String host = "localhost";
        private int port = 22;
        private String user;
        private String password;

    }

    @Data
    public static class FtpConfig {
        private String host = "localhost";
        private int port = 21;
        private String user;
        private String password;
    }
}