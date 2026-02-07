package com.oneself.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class EncryptionUtils {

    /**
     * 私有构造器，防止被实例化
     */
    private EncryptionUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    /**
     * 算法
     */
    public static final String ALGORITHM = "AES/GCM/NoPadding";

    /**
     * 固定密钥
     */
    private static final String SECURITY_KEY = "X1anrenfuw0d1ng_";

    /**
     * 动态生成随机IV（12字节用于GCM模式）
     *
     * @return 随机生成的12字节IV
     */
    private static byte[] generateRandomIV() {
        byte[] iv = new byte[12]; // GCM推荐12字节IV
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    /**
     * 通用加密逻辑，使用AES/GCM/NoPadding加密方式
     *
     * @param content 待加密内容
     * @param sKey    密钥
     * @return 包含IV的加密结果（Base64编码）
     */
    public static String encrypt(String content, String sKey) {
        try {
            // 生成随机IV
            byte[] ivBytes = generateRandomIV();

            // 初始化AES GCM Cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(StandardCharsets.US_ASCII), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);  // 使用128位认证标签

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, gcmSpec);

            // 执行加密
            byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

            // 将IV和密文拼接并Base64编码
            byte[] result = new byte[ivBytes.length + encryptedBytes.length];
            System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, result, ivBytes.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            log.error("加密时出错", e);
            return content;
        }
    }

    /**
     * 默认使用固定密钥加密
     *
     * @param content 待加密内容
     * @return 包含IV的加密结果（Base64编码）
     */
    public static String encrypt(String content) {
        return encrypt(content, SECURITY_KEY);
    }

    /**
     * 通用解密逻辑，使用AES/GCM/NoPadding解密方式
     *
     * @param encryptedContent 包含IV的加密内容（Base64编码）
     * @param sKey             密钥
     * @return 解密后的内容
     */
    public static String decrypt(String encryptedContent, String sKey) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedContent);

            // 提取IV（前12字节）
            byte[] ivBytes = new byte[12];
            System.arraycopy(decodedBytes, 0, ivBytes, 0, 12);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);

            // 提取密文（从第13字节开始）
            byte[] encryptedBytes = new byte[decodedBytes.length - 12];
            System.arraycopy(decodedBytes, 12, encryptedBytes, 0, encryptedBytes.length);

            // 解密
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(StandardCharsets.US_ASCII), "AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmSpec);
            byte[] originalBytes = cipher.doFinal(encryptedBytes);

            return new String(originalBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密时出错", e);
            return encryptedContent;
        }
    }

    /**
     * 默认使用固定密钥解密
     *
     * @param encryptedContent 包含IV的加密内容（Base64编码）
     * @return 解密后的内容
     */
    public static String decrypt(String encryptedContent) {
        return decrypt(encryptedContent, SECURITY_KEY);
    }
}