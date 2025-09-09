package com.oneself.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class JacksonUtils {

    /**
     * 单例 ObjectMapper（静态初始化，线程安全）
     * 配置：支持java.time时间类型、禁用时间戳格式（输出ISO8601字符串）
     */
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        // 注册 JavaTimeModule，解决 LocalDateTime/LocalDate 等类型序列化问题
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 禁用时间戳格式（默认会把时间转为毫秒数，禁用后输出 "2025-09-12T10:00:00" 格式）
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 可选：忽略未知字段（避免 JSON 中有多余字段时反序列化失败）
        OBJECT_MAPPER.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 私有构造方法：防止外部实例化（工具类不允许创建对象）
     */
    private JacksonUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    /**
     * 获取单例 ObjectMapper（供外部自定义配置使用，如添加自定义序列化器）
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    // ------------------------------ 序列化方法 ------------------------------

    /**
     * 1. Java 对象 → JSON 字符串
     *
     * @param object 待序列化的对象（支持普通 POJO、java.time 类型、集合等）
     * @return 标准 JSON 字符串；若序列化失败，抛出自定义异常（不返回 null，避免空指针）
     */
    public static String toJsonString(Object object) {
        if (object == null) {
            throw new OneselfException("序列化失败：待序列化对象为 null");
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Java 对象转 JSON 字符串失败，对象类型：{}，对象内容：{}",
                    object.getClass().getName(), object, e);
            throw new OneselfException("JSON 序列化错误：" + e.getMessage(), e);
        }
    }

    /**
     * 2. Java 对象 → JsonNode（内存中树模型，便于修改字段）
     * 场景：已有 Java 对象，需动态添加/删除字段后再序列化（如接口返回时补充额外字段）
     *
     * @param object 待转换的 Java 对象
     * @return JsonNode（ObjectNode/ArrayNode，根据对象类型自动匹配）
     */
    public static JsonNode toJsonNode(Object object) {
        if (object == null) {
            throw new OneselfException("转换失败：待转换对象为 null");
        }
        // 底层用 valueToTree，直接将对象映射为树模型（无中间 JSON 字符串）
        return OBJECT_MAPPER.valueToTree(object);
    }

    /**
     * 3. JSON 字符串 → JsonNode
     * 场景：解析外部 JSON 字符串
     *
     * @param jsonString 标准 JSON 字符串
     * @return JsonNode（可提取字段、判断类型）
     */
    public static JsonNode fromJsonToNode(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new OneselfException("转换失败：JSON 字符串为空");
        }
        try {
            // 底层用readTree，按JSON语法解析字符串为树模型
            return OBJECT_MAPPER.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.error("JSON 字符串转 JsonNode 失败，JSON 内容：{}", jsonString, e);
            throw new OneselfException("JSON 解析错误：格式非法或语法错误", e);
        }
    }

    // ------------------------------ 反序列化方法 ------------------------------

    /**
     * 4. JSON 字符串 → 单个 Java 对象（支持普通 POJO、java.time 类型）
     *
     * @param jsonString 标准 JSON 字符串
     * @param clazz      目标对象的 Class
     * @param <T>        目标对象类型
     * @return 反序列化后的 Java 对象；失败时抛出异常（不返回 null）
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new OneselfException("反序列化失败：JSON 字符串为空");
        }
        if (clazz == null) {
            throw new OneselfException("反序列化失败：目标类型 Class 为 null");
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON 字符串转 {} 对象失败，JSON 内容：{}", clazz.getName(), jsonString, e);
            throw new OneselfException("JSON 反序列化错误：" + e.getMessage(), e);
        }
    }

    /**
     * 5. JSON 字符串 → List集合（泛型安全，推荐用 TypeReference）
     * 场景：反序列化为泛型List（如List<UserDTO>），避免类型擦除导致的 ClassCastException
     *
     * @param jsonString    标准 JSON 数组字符串
     * @param typeReference 泛型类型引用（如 new TypeReference<List<UserDTO>>(){}）
     * @param <T>           List 中元素的类型
     * @return 泛型 List；失败时返回空 List（避免 null，便于下游遍历）
     */
    public static <T> List<T> fromJsonList(String jsonString, TypeReference<List<T>> typeReference) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            log.warn("JSON 数组字符串为空，返回空 List");
            return Collections.emptyList();
        }
        if (typeReference == null) {
            log.error("TypeReference 为 null，返回空 List");
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON 字符串转泛型 List 失败，JSON 内容：{}，TypeReference：{}",
                    jsonString, typeReference.getType(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 6. JSON 字符串 → List 集合（简化版，无需 TypeReference）
     * 场景：反序列化为简单 List（如 List<String>、List<Long>），避免匿名内部类
     *
     * @param jsonString   标准 JSON 数组字符串
     * @param elementClazz List 中元素的 Class（如String.class、UserDTO.class）
     * @param <T>          List 中元素的类型
     * @return 泛型 List；失败时抛出异常（适合需要明确失败的场景）
     */
    public static <T> List<T> fromJsonList(String jsonString, Class<T> elementClazz) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new OneselfException("反序列化 List 失败：JSON 字符串为空");
        }
        if (elementClazz == null) {
            throw new OneselfException("反序列化 List 失败：元素类型 Class 为 null");
        }
        try {
            // 构建泛型类型：List<elementClazz>
            JavaType listType = OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, elementClazz);
            return OBJECT_MAPPER.readValue(jsonString, listType);
        } catch (JsonProcessingException e) {
            log.error("JSON 字符串转 List<{}> 失败，JSON 内容：{}",
                    elementClazz.getName(), jsonString, e);
            throw new OneselfException("JSON 反序列化 List 错误：" + e.getMessage(), e);
        }
    }

    /**
     * 自定义异常：统一JSON处理相关的异常类型（便于全局捕获）
     */
    public static class OneselfException extends RuntimeException {
        public OneselfException(String message) {
            super(message);
        }

        public OneselfException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}