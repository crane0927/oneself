package com.oneself.client;

import com.oneself.client.fallback.UserClientFallbackFactory;
import com.oneself.model.dto.LoginUserDTO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.model.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.client
 * interfaceName DemoClient
 * description 用户相关 Feign 接口
 * version 1.0
 */
@Tag(name = "feign demo")
@FeignClient(value = "oneself-system", path = "/oneself-system/user",
        fallbackFactory = UserClientFallbackFactory.class
)
public interface UserClient {

    @Operation(summary = "查询登录用户")
    @PostMapping("/get/login/user")
    ResponseVO<UserVO> getLoginUser(@RequestBody @Valid LoginUserDTO dto);
}