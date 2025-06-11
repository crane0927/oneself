package com.oneself.test;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.client.DemoClient;
import com.oneself.model.dto.DemoDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuhuan
 * date 2025/1/24
 * packageName com.oneself.test
 * className TestController
 * description
 * version 1.0
 */
@Tag(name = "Feign 测试")
@Slf4j
@RequiredArgsConstructor
@RequireLogin
@RequestLogging
@RestController
@RequestMapping({"/test"})
public class TestController {

    private final DemoClient demoClient;

    private final RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "你好 xxx")
    @PostMapping({"/feign01"})
    public ResponseVO<DemoVO> feign01(@RequestBody DemoDTO dto) {
        return demoClient.sayHello(dto);

    }

    @Operation(summary = "Redis 测试")
    @GetMapping({"/redis"})
    public ResponseVO<String> getRedisTest() {
        Object taskIdFields = redisTemplate.opsForValue().get("test01");
        return ResponseVO.success(String.valueOf(taskIdFields));

    }
}
