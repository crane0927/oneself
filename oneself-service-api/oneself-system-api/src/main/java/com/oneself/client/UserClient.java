package com.oneself.client;

import com.oneself.client.fallback.UserClientFallbackFactory;
import com.oneself.model.dto.UserDTO;
import com.oneself.resp.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.client
 * interfaceName UserClient
 * description 用户相关 Feign 接口
 * version 1.0
 */
@Tag(name = "feign system")
@FeignClient(value = "oneself-system", path = "/oneself-system/user",
        fallbackFactory = UserClientFallbackFactory.class
)
public interface UserClient {

    /** 原则 6 合规：名词化路径，与 UserController 新路径一致 */
    @Operation(summary = "根据用户名查询用户（RESTful）")
    @GetMapping("/by-name/{name}")
    Resp<UserDTO> getUserByUsername(@PathVariable("name") @Valid @NotBlank String name);

    @Deprecated
    @Operation(summary = "根据用户名查询会话信息（已废弃，请使用 getUserByUsername 即 GET /by-name/{name}）")
    @GetMapping("/get/user/by/{name}")
    Resp<UserDTO> getUserByName(@PathVariable @Valid @NotBlank String name);
}