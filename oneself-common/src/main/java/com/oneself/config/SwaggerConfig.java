package com.oneself.config;

import com.oneself.properties.SwaggerProperties;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.config
 * className SwaggerConfig
 * description Swagger 配置类
 * version 1.0
 */
@Configuration
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    public SwaggerConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("oneself") // 分组名称
                .packagesToScan(swaggerProperties.getBasePackage() != null ? swaggerProperties.getBasePackage() : "com.oneself")
                .addOpenApiCustomizer(openApi -> openApi.info(apiInfo()))
                .pathsToMatch("/**")
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title(swaggerProperties.getTitle() != null ? swaggerProperties.getTitle() : "API Documentation")
                .description(swaggerProperties.getDescription() != null ? swaggerProperties.getDescription() : "API Documentation for Oneself")
                .version(swaggerProperties.getVersion() != null ? swaggerProperties.getVersion() : "1.0")
                .contact(new Contact()
                        .name(swaggerProperties.getContactName())
                        .url(swaggerProperties.getContactUrl())
                        .email(swaggerProperties.getContactEmail()))
                .termsOfService(swaggerProperties.getServiceUrl() != null ? swaggerProperties.getServiceUrl() : "https://doc.xiaominfo.com/")
                .license(new License()
                        .name(swaggerProperties.getLicense())
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }
}