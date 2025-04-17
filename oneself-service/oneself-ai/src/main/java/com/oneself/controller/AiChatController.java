package com.oneself.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


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

    @Operation(summary = "非流式")
    @GetMapping("/call")
    public String call(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Operation(summary = "流式")
    @GetMapping(value = "/stream", produces = "text/html;charset=utf-8")
    public Flux<String> stream(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }
}
