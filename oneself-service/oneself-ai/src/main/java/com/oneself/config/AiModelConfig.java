package com.oneself.config;

import com.oneself.chat.memory.InMySQLChatMemory;
import com.oneself.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
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
@RequiredArgsConstructor
@Configuration
public class AiModelConfig {

    private final ChatMessageMapper chatMessageMapper;

    @Bean
    public ChatMemory chatMemory() {
//        return new InMemoryChatMemory(); // 会话存储方式 内存存储
        return new InMySQLChatMemory(chatMessageMapper);
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel model) {
        return ChatClient.builder(model)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory())) // 日志记录器
                .build();
    }


}
