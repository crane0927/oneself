package com.oneself.controller;

import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.EncryptedDataDTO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.utils.RSAUtils;
import com.oneself.utils.SM4Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.util.Base64;

/**
 * @author liuhuan
 * date 2025/1/3
 * packageName com.oneself.controller
 * className RSAController
 * description RSA 信息接口
 * version 1.0
 */
@Api(tags = "RSA 信息接口")
@Slf4j
@RequireLogin
@RestController
@RequestMapping({"/rsa/info"})
public class RSAController {

    private final KeyPair keyPair;

    public RSAController() throws Exception {
        this.keyPair = RSAUtils.generateRSAKeyPair();
    }

    @ApiOperation(value = "获取 RSA 公钥")
    @GetMapping("/get/public/key")
    public ResponseVO<String> getPublicKey() {
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        return ResponseVO.success(publicKey);
    }

    @ApiOperation(value = "解密数据")
    @PostMapping("/decrypt/data")
    public ResponseVO<String> decryptData(@RequestBody EncryptedDataDTO dto) throws Exception {
        String decryptedSymmetricKey = RSAUtils.decryptWithPrivateKey(
                java.util.Base64.getDecoder().decode(dto.getEncryptedSymmetricKey()),
                keyPair.getPrivate()
        );

        return ResponseVO.success(SM4Utils.decryptEcb(decryptedSymmetricKey, dto.getEncryptedData()));
    }

}
