package com.oneself.system;

import com.oneself.common.feature.web.utils.ApplicationStartupUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.annotation.Validated;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself
 * className SystemApplication
 * description
 * version 1.0
 */
@EnableFeignClients(basePackages = "com.oneself.client")
@EnableDiscoveryClient
@SpringBootApplication
@Validated
public class SystemApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(SystemApplication.class, args);
        // 使用工具类 打印启动信息
        ApplicationStartupUtils.printStartupInfo(application.getEnvironment());
    }
}
