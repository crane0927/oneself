package com.oneself.client.fallback;

import com.oneself.client.DemoClient;
import com.oneself.model.dto.SensitiveDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liuhuan
 * date 2025/3/27
 * packageName com.oneself.client.fallback
 * className DemoClientFallback
 * description Demo 请求降级
 * version 1.0
 */
@Slf4j
@Component
public class DemoClientFallback implements DemoClient {
    @Override
    public ResponseVO<DemoVO> sensitive(SensitiveDTO dto) {
        return ResponseVO.failure("DemoClientFallback sayHello error");
    }
}
