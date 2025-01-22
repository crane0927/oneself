package com.oneself.exception;

import java.io.Serial;

/**
 * @author liuhuan
 * date 2025/1/22
 * packageName com.oneself.exception
 * className BusinessException
 * description 自定义业务异常
 * version 1.0
 */
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public BusinessException(String message) {
        super(message);
    }
}
