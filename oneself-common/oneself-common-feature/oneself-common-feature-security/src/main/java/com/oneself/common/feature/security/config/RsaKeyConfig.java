package com.oneself.common.feature.security.config;

import com.oneself.common.feature.security.utils.RsaUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2025/9/12
 * packageName com.oneself.config
 * className RsaKeyConfig
 * description
 * version 1.0
 */
@Slf4j
@Getter
@Configuration
public class RsaKeyConfig {

    private final String privateKey;
    private final String publicKey;

    public RsaKeyConfig(
            @Value("${ONESELF_RSA_PRIVATE_KEY:}") String privateKey,
            @Value("${ONESELF_PUBLIC_KEY:}") String publicKey) {
        if (privateKey.isEmpty() || publicKey.isEmpty()) {
            throw new IllegalStateException("ONESELF_RSA_PRIVATE_KEY 或 ONESELF_PUBLIC_KEY 未配置");
        }
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        log.info("RSA KeyConfig 初始化成功");
    }

    public String encrypt(String data) {
        try {
            return RsaUtils.encryptByPublicKey(data, publicKey);
        } catch (Exception e) {
            log.error("RSA 公钥加密失败", e);
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            return RsaUtils.decryptByPrivateKey(encryptedData, privateKey);
        } catch (Exception e) {
            log.error("RSA 私钥解密失败", e);
            throw new RuntimeException(e);
        }
    }
}
