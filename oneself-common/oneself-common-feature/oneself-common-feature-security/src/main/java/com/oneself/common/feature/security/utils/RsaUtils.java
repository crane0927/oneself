package com.oneself.common.feature.security.utils;

import com.oneself.common.feature.security.config.RsaKeyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author liuhuan
 * date 2025/9/12
 * packageName com.oneself.utils
 * className RsaUtils
 * description RSA 非对称加密算法
 * version 1.0
 */
@Component
@RequiredArgsConstructor
public class RsaUtils {

    /**
     * 算法
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    /**
     * 密钥长度
     */
    private static final int KEY_SIZE = 2048;

    /**
     * RSA 最大加密块大小 (2048位 -> 245字节)
     */
    private static final int MAX_ENCRYPT_BLOCK = 245;
    /**
     * RSA 最大解密块大小 (2048位 -> 256字节)
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    private final RsaKeyConfig rsaKeyConfig;

    /**
     * 生成 RSA 密钥对
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGen.initialize(KEY_SIZE);
            return keyPairGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("生成密钥对失败", e);
        }
    }

    /**
     * 公钥加密（分段）
     */
    public static String encryptByPublicKey(String data, String publicKeyStr) {
        try {
            PublicKey publicKey = getPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            int inputLen = dataBytes.length;
            int offset = 0;
            byte[] cache;
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(dataBytes, offset, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                offset += MAX_ENCRYPT_BLOCK;
            }
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("公钥加密失败", e);
        }
    }


    /**
     * 私钥解密（分段）
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKeyStr) {
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            int inputLen = encryptedBytes.length;
            int offset = 0;
            byte[] cache;
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedBytes, offset, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedBytes, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                offset += MAX_DECRYPT_BLOCK;
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("私钥解密失败", e);
        }
    }

    /**
     * 私钥签名
     */
    public static String sign(String data, String privateKeyStr) {
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("签名失败", e);
        }
    }

    /**
     * 公钥验签
     */
    public static boolean verify(String data, String sign, String publicKeyStr) {
        try {
            PublicKey publicKey = getPublicKey(publicKeyStr);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            throw new RuntimeException("验签失败", e);
        }
    }

    /**
     * 公钥对象
     */
    public static PublicKey getPublicKey(String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance(ALGORITHM).generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("加载公钥失败", e);
        }
    }

    /**
     * 私钥对象
     */
    public static PrivateKey getPrivateKey(String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance(ALGORITHM).generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("加载私钥失败", e);
        }
    }

    /**
     * 公钥 Base64
     */
    public static String getBase64PublicKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    /**
     * 私钥 Base64
     */
    public static String getBase64PrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }


    public static void main(String[] args) {
        // 1. 生成密钥对
        KeyPair keyPair = RsaUtils.generateKeyPair();
        String publicKey = RsaUtils.getBase64PublicKey(keyPair);
        String privateKey = RsaUtils.getBase64PrivateKey(keyPair);

        System.out.println("公钥: " + publicKey);
        System.out.println("私钥: " + privateKey);

        // 2. 公钥加密 + 私钥解密
        String text = "oneself!@#";
        String encrypted = RsaUtils.encryptByPublicKey(text, publicKey);
        String decrypted = RsaUtils.decryptByPrivateKey(encrypted, privateKey);

        System.out.println("加密后: " + encrypted);
        System.out.println("解密后: " + decrypted);

        // 3. 私钥签名 + 公钥验签
        String sign = RsaUtils.sign(text, privateKey);
        boolean verified = RsaUtils.verify(text, sign, publicKey);
        System.out.println("签名: " + sign);
        System.out.println("验签结果: " + verified);
    }
}
