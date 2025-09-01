package com.oneself.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.common.model.vo
 * className ResponseVO
 * description
 * version 1.0
 */
@Data
@Schema(description = "响应数据")
public class ResponseVO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码", example = "500")
    private int msgCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    @Schema(description = "状态码说明", example = "请求失败")
    private String message = "请求失败";
    @Schema(description = "请求路径", example = "/oneself")
    private String path = null;
    @Schema(description = "返回数据", example = "false")
    private transient T data;
    @Schema(description = "请求追踪 ID")
    private String traceId;

    ResponseVO() {
    }

    private ResponseVO(int msgCode, String message, T data) {
        this.msgCode = msgCode;
        this.message = message;
        this.data = data;
    }

    private ResponseVO(int msgCode, String message, String path, String traceId) {
        this.msgCode = msgCode;
        this.message = message;
        this.path = path;
        this.traceId = traceId;
    }

    /**
     * 静态方法，返回一个表示成功的 ResponseVO 实例
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功的 ResponseVO 实例
     */
    public static <T> ResponseVO<T> success(T data) {
        // 返回状态码 200，表示成功，并且设置 message 为 "请求成功"
        return new ResponseVO<>(HttpStatus.OK.value(), "请求成功", data);
    }

    /**
     * 静态方法，返回一个表示失败的 ResponseVO 实例
     *
     * @param errorMessage 错误信息
     * @param <T>          数据类型
     * @return 失败的 ResponseVO 实例
     */
    public static <T> ResponseVO<T> failure(String errorMessage) {
        // 返回状态码 500，表示服务器内部错误，并设置相应的错误信息
        return new ResponseVO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, null);
    }

    /**
     * 静态方法，返回一个表示失败的 ResponseVO 实例 并自定义状态码
     *
     * @param errorMessage 错误信息
     * @param status       Http 状态码
     * @param <T>          数据类型
     * @return 失败的 ResponseVO 实例
     */
    public static <T> ResponseVO<T> failure(String errorMessage, HttpStatus status) {
        // 返回状态码 500，表示服务器内部错误，并设置相应的错误信息
        return new ResponseVO<>(status.value(), errorMessage, null);
    }

    /**
     * 静态方法，返回一个表示失败的 ResponseVO 实例 并自定义状态码
     *
     * @param errorMessage 错误信息
     * @param status       Http 状态码
     * @param <T>          数据类型
     * @return 失败的 ResponseVO 实例
     */
    public static <T> ResponseVO<T> failure(String errorMessage, HttpStatus status, String path, String traceId) {
        // 返回状态码 500，表示服务器内部错误，并设置相应的错误信息
        return new ResponseVO<>(status.value(), errorMessage, path, traceId);
    }

}
