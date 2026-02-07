package com.oneself.gateway.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.config
 * className GatewayInfoContributor
 * description Gateway 应用信息贡献者（用于 Actuator /actuator/info 端点）
 * version 1.0
 */
@Component
public class GatewayInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        details.put("name", "oneself-gateway");
        details.put("description", "API Gateway for oneself microservices");
        details.put("version", "1.0.0");
        details.put("buildTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        details.put("features", new String[]{
            "JWT Authentication",
            "CORS Support",
            "Rate Limiting",
            "Request Tracing",
            "Metrics Collection",
            "Health Check"
        });
        
        builder.withDetails(details);
    }
}

