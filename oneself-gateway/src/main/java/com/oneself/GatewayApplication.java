package com.oneself;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Slf4j
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(GatewayApplication.class, args);

        Environment env = application.getEnvironment();

        // 获取本机 IP 地址
        String ip = Optional.ofNullable(getLocalHostAddress()).orElse("localhost");

        // 获取端口号和上下文路径
        String port = env.getProperty("server.port", "8080");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse("");

        // 构建 URL 路径
        String baseUrl = String.format("http://%s:%s%s", ip, port, contextPath);
        String swaggerUrl = baseUrl + "/doc.html";

        // 打印启动信息
        log.info("\n----------------------------------------------------------\n\t" +
                        "SQL审核采集服务启动成功:\n\t" +
                        "本机 IP 地址：{}\n\t" +
                        "项目接口路径：{}\n\t" +
                        "Swagger 文档：{}\n" +
                        "----------------------------------------------------------",
                ip, baseUrl, swaggerUrl);
    }

    /**
     * 获取本机 IP 地址的
     *
     * @return 本机 IP 地址
     */
    private static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("无法获取本机 IP 地址", e);
            return null;
        }
    }

}
