package com.oneself.common.core.exception;

import java.io.Serial;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.exception
 * className OneselfLoginException
 * description 
 * version 1.0
 */
public class OneselfLoginException extends RuntimeException {

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
    public OneselfLoginException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method)
     */
    public OneselfLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
