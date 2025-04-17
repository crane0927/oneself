package com.oneself.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuhuan
 * date 2025/4/16
 * packageName com.oneself.config
 * className AiModelConfig
 * description AI 模型配置
 * version 1.0
 */
@Configuration
public class AiModelConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel model) {
        return ChatClient.builder(model)
                .build();
    }
}
