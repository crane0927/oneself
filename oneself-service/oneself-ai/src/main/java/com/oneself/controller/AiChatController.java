package com.oneself.controller;

import com.oneself.model.enums.ChatTypeEnum;
import com.oneself.service.ChatHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;


/**
 * @author liuhuan
 * date 2025/4/16
 * packageName com.oneself.controller
 * className AiChatController
 * description AI 对话接口
 * version 1.0
 */
@Tag(name = "AI 对话")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/chat")
public class AiChatController {

    private final ChatClient chatClient;
    private final ChatHistoryService chatHistoryService;

    @Operation(summary = "非流式")
    @GetMapping("/call")
    public String call(@Schema(description = "用户输入的提示词", requiredMode = Schema.RequiredMode.REQUIRED) @RequestParam @NotNull String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Operation(summary = "流式")
    @GetMapping(value = "/stream", produces = "text/html;charset=utf-8")
    public Flux<String> stream(@Schema(description = "用户输入的提示词", requiredMode = Schema.RequiredMode.REQUIRED) @RequestParam @NotNull String prompt,
                               @Schema(description = "聊天会话 ID", requiredMode = Schema.RequiredMode.REQUIRED) @RequestParam @NotNull String chatId) {
        chatHistoryService.save(chatId, "对话-" + chatId, ChatTypeEnum.CHAT);
        return chatClient.prompt()
                .user(prompt)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
    }
}
