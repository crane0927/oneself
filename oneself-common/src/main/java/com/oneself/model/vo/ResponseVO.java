package com.oneself.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

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
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码", example = "500")
    private Integer msgCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    @Schema(description = "状态码说明", example = "请求失败")
    private String message = "请求失败";
    @Schema(description = "请求路径", example = "/snc-audit")
    private String path = null;
    @Schema(description = "返回数据", example = "false")
    private T data;

    ResponseVO() {
//        throw new AssertionError("此工具类不允许实例化");
    }

    private ResponseVO(int msgCode, String message, String path, T data) {
        this.msgCode = msgCode;
        this.message = message;
        this.path = path;
        this.data = data;
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
        return new ResponseVO<>(HttpStatus.OK.value(), "请求成功", null, data);
    }

    /**
     * 静态方法，返回一个带有自定义路径的成功响应
     *
     * @param data 响应数据
     * @param path 请求路径
     * @param <T>  数据类型
     * @return 成功的 ResponseVO 实例
     */
    public static <T> ResponseVO<T> success(T data, String path) {
        // 返回带有路径信息的成功响应
        return new ResponseVO<>(HttpStatus.OK.value(), "请求成功", path, data);
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
        return new ResponseVO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, null, null);
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
        return new ResponseVO<>(status.value(), errorMessage, null, null);
    }

}
