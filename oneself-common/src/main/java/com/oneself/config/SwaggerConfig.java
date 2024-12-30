package com.oneself.config;

import com.oneself.properties.SwaggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.config
 * className SwaggerConfig
 * description Swagger 配置类
 * version 1.0
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    @Autowired
    public SwaggerConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean(value = "dockerBean")
    public Docket dockerBean() {
        // 使用 SwaggerProperties 中的配置来动态配置 Docket
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerProperties.getEnable())
                .apiInfo(new ApiInfoBuilder()
                        // 使用 SwaggerProperties 中的配置字段
                        .title(swaggerProperties.getTitle() != null ? swaggerProperties.getTitle() : "API Documentation")
                        .description(swaggerProperties.getDescription())
                        .termsOfServiceUrl(swaggerProperties.getServiceUrl() != null ? swaggerProperties.getServiceUrl() : "https://doc.xiaominfo.com/")
                        .contact(new Contact(
                                swaggerProperties.getContactName(),
                                swaggerProperties.getContactUrl(),
                                swaggerProperties.getContactEmail()))
                        .license(swaggerProperties.getLicense())
                        .version(swaggerProperties.getVersion() != null ? swaggerProperties.getVersion() : "1.0")
                        .build())
                .groupName("oneself")
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage() != null ? swaggerProperties.getBasePackage() : "com.oneself"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }
}
