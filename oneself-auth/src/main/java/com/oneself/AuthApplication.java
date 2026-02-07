package com.oneself;

import com.oneself.common.feature.web.utils.ApplicationStartupUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.annotation.Validated;

@EnableFeignClients(basePackages = {"com.oneself.client", "com.oneself.system.client"})
@EnableDiscoveryClient
@SpringBootApplication
@Validated
public class AuthApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext application = SpringApplication.run(AuthApplication.class, args);
		ApplicationStartupUtils.printStartupInfo(application.getEnvironment());
	}

}
