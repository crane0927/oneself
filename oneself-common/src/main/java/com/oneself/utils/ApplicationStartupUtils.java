package com.oneself.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author liuhuan
 * date 2024/12/31
 * packageName com.oneself.utils
 * className ApplicationStartupUtils
 * description 应用程序启动配置工具类
 * version 1.0
 */
@Slf4j
public class ApplicationStartupUtils {
    /**
     * 打印启动信息
     *
     * @param env Spring 环境变量
     */
    public static void printStartupInfo(Environment env) {
        // 获取本机 IP 地址
        String ip = Optional.ofNullable(getLocalHostAddress()).orElse("localhost");

        // 获取端口号和上下文路径
        String port = env.getProperty("server.port", "8080");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse("");
        String applicationName = Optional.ofNullable(env.getProperty("spring.application.name")).orElse("oneself-xxx");

        // 构建 URL 路径
        String baseUrl = String.format("http://%s:%s%s", ip, port, contextPath);
        String swaggerUrl = baseUrl + "/doc.html";

        // 打印启动信息
        System.out.printf("""
                                                  ┓┏
                                         ┏┓┏┓┏┓┏┏┓┃╋
                                         ┗┛┛┗┗ ┛┗ ┗┛  (v1.0.0)
                ***************************************************************
                    %s 服务启动成功:
                    本机 IP 地址：%s
                    项目接口路径：%s
                    Swagger 文档：%s
                ***************************************************************
                %n""", applicationName, ip, baseUrl, swaggerUrl);
    }

    /**
     * 获取本机 IP 地址
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