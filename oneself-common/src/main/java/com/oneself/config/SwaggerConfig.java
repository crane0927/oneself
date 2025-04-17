package com.oneself.config;

import com.oneself.properties.SwaggerProperties;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.config
 * className SwaggerConfig
 * description Swagger 主页信息配置类
 * version 1.0
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(
        name = {"oneself.swagger.enable"},
        matchIfMissing = true
)
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group(swaggerProperties.getGroupName())
                .packagesToScan(swaggerProperties.getBasePackage())
                .addOpenApiCustomizer(openApi -> openApi.info(apiInfo()))
                .pathsToMatch("/**")
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .contact(new Contact()
                        .name(swaggerProperties.getContactName())
                        .url(swaggerProperties.getContactUrl())
                        .email(swaggerProperties.getContactEmail()))
                .termsOfService(swaggerProperties.getServiceUrl())
                .license(new License()
                        .name(swaggerProperties.getLicense())
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }
}