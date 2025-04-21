package com.oneself;

import com.oneself.utils.ApplicationStartupUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.annotation.Validated;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@Validated
public class AiApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext application = SpringApplication.run(AiApplication.class, args);
		ApplicationStartupUtils.printStartupInfo(application.getEnvironment());
	}

}
