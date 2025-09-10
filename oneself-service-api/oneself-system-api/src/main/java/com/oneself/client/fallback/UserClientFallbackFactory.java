package com.oneself.client.fallback;

import com.oneself.client.UserClient;
import com.oneself.model.vo.ResponseVO;
import com.oneself.model.vo.UserVO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.client.fallback
 * className UserClientFallbackFactory
 * description User 连接降级处理工厂类
 * version 1.0
 */
@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public ResponseVO<UserVO> getLoginUserByName(String username) {
                if (cause instanceof FeignException.NotFound) {
                    return ResponseVO.failure("服务不存在", HttpStatus.NOT_FOUND);
                } else if (cause instanceof TimeoutException) {
                    return ResponseVO.failure("请求超时", HttpStatus.REQUEST_TIMEOUT);
                } else {
                    return ResponseVO.failure("服务不可用", HttpStatus.SERVICE_UNAVAILABLE);
                }
            }
        };
    }
}
