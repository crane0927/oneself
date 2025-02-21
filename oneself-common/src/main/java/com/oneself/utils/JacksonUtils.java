package com.oneself.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oneself.exception.OneselfException;

import java.util.Collections;
import java.util.List;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.utils
 * className JacksonUtils
 * description
 * 单例模式 获取唯一的 ObjectMapper 实例
 * 本类包括 JSON 转换的常见操作：对象与 JSON 字符串之间的转换，JSON 字符串与对象之间的转换
 * version 1.0
 */
public class JacksonUtils {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        // 注册 JavaTimeModule 以支持 java.time 类型
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 禁用默认的时间戳格式
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 私有构造方法，防止外部实例化
     */
    private JacksonUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    /**
     * 获取唯一的 ObjectMapper 实例
     *
     * @return 唯一的 ObjectMapper 实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 序列化 将对象转换为 JSON 字符串
     *
     * @param object 要序列化的对象
     * @return 对应的 JSON 字符串
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
            return null;
        }
    }

    /**
     * 序列化 将对象转换为 JsonNode
     *
     * @param object 要序列化的对象
     * @return 对应的 JsonNode
     */
    public static JsonNode toJsonNode(Object object) {
        return OBJECT_MAPPER.valueToTree(object);
    }

    /**
     * 反序列化 将 JSON 字符串转换为具体对象
     *
     * @param jsonString JSON 字符串
     * @param clazz      目标对象的类型
     * @param <T>        目标对象的类型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
            return null;
        }
    }

    /**
     * 反序列化 将 JSON 字符串转换为指定类型的 List
     *
     * @param jsonString JSON 字符串
     * @param typeReference TypeReference 用于处理 List 泛型类型
     * @param <T>        列表中元素的类型
     * @return 反序列化后的 List
     */
    public static <T> List<T> fromJsonList(String jsonString, TypeReference<List<T>> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, typeReference);
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(e);
            return Collections.emptyList();
        }
    }

    /**
     * 反序列化 将 JSON 字符串转换为指定类型的 List
     *
     * @param jsonString JSON 字符串
     * @param clazz      列表中元素的类型
     * @param <T>        列表中元素的类型
     * @return 反序列化后的 List
     */
    public static <T> List<T> fromJsonList(String jsonString, Class<T> clazz) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return OBJECT_MAPPER.readValue(jsonString, javaType);
        } catch (JsonProcessingException e) {
            throw new OneselfException("JSON 反序列化失败", e);
        }
    }

    /**
     * 捕获并抛出 JSON 处理相关的异常
     *
     * @param e 异常对象
     */
    private static void handleJsonProcessingException(JsonProcessingException e) {
        // 可以将异常记录到日志或者包装成自定义异常
        throw new OneselfException("JSON 处理错误", e);
    }
}