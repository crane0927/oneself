package com.oneself.chat.memory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneself.mapper.ChatMessageMapper;
import com.oneself.model.pojo.ChatMessage;
import com.oneself.model.vo.ChatMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.service.memory
 * className InMySQLChatMemory
 * description MySQL 存储 AI 会话实现类
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InMySQLChatMemory implements ChatMemory {

    private final ChatMessageMapper chatMessageMapper;

    @Override
    public void add(String conversationId, List<Message> messages) {
        // TODO 获取用户 Id
        String userId = "1";
        List<ChatMessage> list = messages.stream().map(
                message -> ChatMessage.builder()
                        .userId(userId)
                        .conversationId(conversationId)
                        .role(message.getMessageType())
                        .message(message.getText())
                        .build()
        ).toList();
        chatMessageMapper.insert(list);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        // TODO 获取用户 Id
        String userId = "1";
        List<ChatMessage> list = chatMessageMapper.selectList(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getUserId, userId)
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime)
                .last("limit " + lastN)
        );

        if (CollectionUtils.isEmpty(list)) {
            return List.of();
        }

        return list.stream()
                .map(ChatMessageVO::from)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {

    }
}
