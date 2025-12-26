package com.oneself.utils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.utils
 * className NetworkUtils
 * description 网络工具类
 * version 1.0
 */
@Slf4j
public class NetworkUtils {

    private NetworkUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }


    /**
     * 测试主机是否可达
     *
     * @param host    主机地址
     * @param timeout 超时时间，单位毫秒
     * @return true 如果主机可达，否则 false
     */
    public static boolean isHostReachable(String host, int timeout) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isReachable(timeout);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 测试主机端口是否可访问
     *
     * @param host    主机地址
     * @param port    端口号
     * @param timeout 超时时间，单位毫秒
     * @return true 如果端口可访问，否则 false
     */
    public static boolean isPortReachable(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), timeout);
            return true;
        } catch (UnknownHostException e) {
            log.error("Unknown host: {}", host);
        } catch (IOException e) {
            log.error("I/O error: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 测试通过SSH登录主机是否成功
     *
     * @param host     主机地址
     * @param port     端口号
     * @param user     SSH用户名
     * @param password SSH密码
     * @return true 如果SSH登录成功，否则 false
     */
    public static boolean canLoginViaSSH(String host, int port, String user, String password) {
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            // 30秒的超时连接时间
            session.connect(30000);
            return session.isConnected();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return false;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
