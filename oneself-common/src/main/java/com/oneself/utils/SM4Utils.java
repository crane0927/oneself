package com.oneself.utils;

import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import java.nio.charset.StandardCharsets;

/**
 * @author liuhuan
 * date 2025/1/3
 * packageName com.oneself.utils
 * className SM4Utils
 * description SM4 加解密工具类
 * version 1.0
 */
public class SM4Utils {

    /**
     * SM4 密钥长度为 16 字节
     */
    private static final int KEY_SIZE = 16;

    /**
     * SM4 加密
     *
     * @param key  对称密钥（必须是 16 字节）
     * @param data 待加密的数据
     * @return 加密后的数据
     * @throws Exception 如果密钥或数据无效
     */
    public static byte[] encryptEcb(String key, String data) throws Exception {
        validateKey(key);

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new SM4Engine());
        cipher.init(true, new KeyParameter(keyBytes));

        byte[] encryptedData = new byte[cipher.getOutputSize(dataBytes.length)];
        int len = cipher.processBytes(dataBytes, 0, dataBytes.length, encryptedData, 0);
        len += cipher.doFinal(encryptedData, len);

        byte[] result = new byte[len];
        System.arraycopy(encryptedData, 0, result, 0, len);
        return result;
    }

    /**
     * SM4 解密
     *
     * @param key           对称密钥（必须是 16 字节）
     * @param encryptedData 加密的数据
     * @return 解密后的明文
     * @throws Exception 如果密钥或数据无效
     */
    public static String decryptEcb(String key, byte[] encryptedData) throws Exception {
        validateKey(key);

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new SM4Engine());
        cipher.init(false, new KeyParameter(keyBytes));

        byte[] decryptedData = new byte[cipher.getOutputSize(encryptedData.length)];
        int len = cipher.processBytes(encryptedData, 0, encryptedData.length, decryptedData, 0);
        len += cipher.doFinal(decryptedData, len);

        return new String(decryptedData, 0, len, StandardCharsets.UTF_8);
    }

    /**
     * 验证密钥是否合法
     *
     * @param key 密钥
     * @throws IllegalArgumentException 如果密钥长度不合法
     */
    private static void validateKey(String key) {
        if (key == null || key.getBytes().length != KEY_SIZE) {
            throw new IllegalArgumentException("SM4 密钥长度必须是 16 字节");
        }
    }
}