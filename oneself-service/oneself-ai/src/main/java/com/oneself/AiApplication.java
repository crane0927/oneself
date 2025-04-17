package com.oneself;

import com.oneself.utils.ApplicationStartupUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.annotation.Validated;

//@EnableFeignClients
//@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Validated
public class AiApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext application = SpringApplication.run(AiApplication.class, args);
		// 使用工具类 打印启动信息
		ApplicationStartupUtils.printStartupInfo(application.getEnvironment());
	}

}
