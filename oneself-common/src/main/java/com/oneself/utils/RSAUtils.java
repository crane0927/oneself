package com.oneself.utils;

import javax.crypto.Cipher;
import java.security.*;

/**
 * @author liuhuan
 * date 2025/1/3
 * packageName com.oneself.utils
 * className RSAUtils
 * description RSA 加解密工具类
 * version 1.0
 */
public class RSAUtils {
    private static final String ALGORITHM = "RSA";

    /**
     * 生成 RSA 密钥对
     *
     * @return KeyPair 公私密钥对
     * @throws NoSuchAlgorithmException 算法不存在异常
     */
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        // 密钥长度 2048 位
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 公钥加密
     *
     * @param data      原始数据
     * @param publicKey 公钥
     * @return 加密后的数据
     * @throws Exception 加密异常
     */
    public static byte[] encryptWithPublicKey(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 加密后的数据
     * @param privateKey    私钥
     * @return 解密后的数据
     * @throws Exception 解密异常
     */
    public static String decryptWithPrivateKey(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData);
    }
}
