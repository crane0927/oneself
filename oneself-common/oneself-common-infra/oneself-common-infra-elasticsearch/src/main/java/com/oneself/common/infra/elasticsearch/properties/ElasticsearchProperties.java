package com.oneself.common.infra.elasticsearch.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/1/2
 * packageName com.oneself.properties
 * className ElasticsearchProperties
 * description Elasticsearch 配置
 * version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "oneself.elasticsearch")
public class ElasticsearchProperties {
    /**
     * 是否启用 Elasticsearch 配置
     */
    private boolean enable;

    /**
     * 节点地址（单机模式支持一个地址，集群模式支持多个地址）
     * 示例: ["localhost:9200", "127.0.0.1:9200"]
     */
    private List<String> nodes;

    /**
     * 用户名（如果启用了安全认证）
     */
    private String username;

    /**
     * 密码（如果启用了安全认证）
     */
    private String password;

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读超时时间（毫秒）
     */
    private int socketTimeout = 60000;

    /**
     * 最大连接数
     */
    private int maxConnections = 100;

    /**
     * 每个路由的最大连接数
     */
    private int maxConnectionsPerRoute = 10;
}
