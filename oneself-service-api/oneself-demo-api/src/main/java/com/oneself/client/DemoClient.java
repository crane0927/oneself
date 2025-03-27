package com.oneself.client;

import com.oneself.client.fallback.DemoClientFallbackFactory;
import com.oneself.model.dto.DemoDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
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
 * description 接口样例
 * version 1.0
 */
@Tag(name = "feign demo")
@FeignClient(value = "oneself-demo", path = "/oneself-demo/demo",
//        fallback = DemoClientFallback.class,
        fallbackFactory = DemoClientFallbackFactory.class
)
public interface DemoClient {


    @Operation(summary = "你好 xxx")
    @PostMapping("/say/hello")
    ResponseVO<DemoVO> sayHello(@RequestBody @Valid DemoDTO dto);
}