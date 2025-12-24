package com.oneself.utils;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.oneself.properties.ElasticsearchProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author liuhuan
 * date 2025/1/2
 * packageName com.oneself.utils
 * className ElasticsearchUtils
 * description Elasticsearch 工具类
 * version 1.0
 */

@Slf4j
@Component
public class ElasticsearchUtils {

    private final ElasticsearchProperties properties;

    /**
     * 线程安全的单例
     */
    private final AtomicReference<ElasticsearchClient> client = new AtomicReference<>();

    @Autowired
    public ElasticsearchUtils(ElasticsearchProperties properties) {
        this.properties = properties;
    }

    /**
     * 初始化 Elasticsearch 客户端
     */
    @PostConstruct
    public void init() {
        if (client.get() == null && properties.isEnable()) {
            synchronized (this) {
                if (client.get() == null) {
                    log.info("初始化 Elasticsearch 客户端");
                    // 构建 RestClient
                    RestClientBuilder restClientBuilder = RestClient.builder(
                            properties.getNodes().stream()
                                    .map(node -> {
                                        String[] parts = node.split(":");
                                        if (parts.length != 2) {
                                            throw new IllegalArgumentException("无效的节点格式: " + node);
                                        }
                                        return new org.apache.http.HttpHost(parts[0], Integer.parseInt(parts[1]));
                                    })
                                    .toArray(org.apache.http.HttpHost[]::new)
                    );

                    // 配置安全认证（如果启用）
                    if (properties.getUsername() != null && properties.getPassword() != null) {
                        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(
                                AuthScope.ANY,
                                new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword())
                        );

                        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder ->
                                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                        .setMaxConnTotal(properties.getMaxConnections())
                                        .setMaxConnPerRoute(properties.getMaxConnectionsPerRoute())
                        );
                    }

                    // 设置连接超时
                    restClientBuilder.setRequestConfigCallback(requestConfigBuilder ->
                            requestConfigBuilder
                                    .setConnectTimeout(properties.getConnectTimeout())
                                    .setSocketTimeout(properties.getSocketTimeout())
                    );

                    RestClient restClient = restClientBuilder.build();
                    RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
                    client.set(new ElasticsearchClient(transport));
                    log.info("Elasticsearch 客户端初始化完成");
                }
            }
        }
    }

    /**
     * 获取 Elasticsearch 客户端
     */
    public ElasticsearchClient getClient() {
        ElasticsearchClient localClient = client.get();
        if (localClient == null) {
            throw new IllegalStateException("Elasticsearch 客户端未初始化");
        }
        return localClient;
    }
}