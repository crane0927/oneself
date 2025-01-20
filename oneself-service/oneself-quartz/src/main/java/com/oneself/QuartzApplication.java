package com.oneself;

import com.oneself.utils.ApplicationStartupUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.annotation.Validated;

@EnableDiscoveryClient
@SpringBootApplication
@Validated
public class QuartzApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(QuartzApplication.class, args);
        // 使用工具类 打印启动信息
        ApplicationStartupUtils.printStartupInfo(application.getEnvironment());
    }

}

