package com.oneself.common.core.exception;

/**
 * @author liuhuan
 * date 2025/1/22
 * packageName com.oneself.exception
 * className OneselfException
 * description 自定义 FTP 异常
 * version 1.0
 */
public class OneselfFtpUploadException extends Exception {
    public OneselfFtpUploadException(String message) {
        super(message);
    }

    public OneselfFtpUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
