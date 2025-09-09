package com.oneself.client.fallback;

import com.oneself.client.DemoClient;
import com.oneself.model.dto.SensitiveDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;

/**
 * @author liuhuan
 * date 2025/3/27
 * packageName com.oneself.client.fallback
 * className DemoClientFallbackFactory
 * description Demo 连接降级处理工厂类
 * version 1.0
 */
@Slf4j
@Component
public class DemoClientFallbackFactory implements FallbackFactory<DemoClient> {
    @Override
    public DemoClient create(Throwable cause) {
        return new DemoClient() {

            @Override
            public ResponseVO<DemoVO> sensitive(SensitiveDTO dto) {
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
