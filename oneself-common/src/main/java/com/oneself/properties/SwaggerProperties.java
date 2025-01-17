package com.oneself.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.properties
 * className SwaggerProperties
 * description
 * version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "oneself.swagger")
public class SwaggerProperties {
    private Boolean enable = Boolean.FALSE;
    private String basePackage = "com.oneself";
    private String title = "oneself";
    private String description = "oneself description";
    private String license;
    private String serviceUrl;
    private String contactName = "crane";
    private String contactUrl = "https://github.com/crane0927";
    private String contactEmail = "crane0927@163.com";
    private String version = "1.0";
    private String groupName = "oneself";
}
