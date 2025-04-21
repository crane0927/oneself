package com.oneself.service.impl;

import com.alibaba.nacos.shaded.com.google.protobuf.Message;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneself.mapper.ChatHistoryMapper;
import com.oneself.model.enums.ChatTypeEnum;
import com.oneself.model.pojo.ChatHistory;
import com.oneself.model.vo.ChatHistoryVO;
import com.oneself.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.service.impl
 * className ChatHistoryServiceImpl
 * description 会话历史接口实现类
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryMapper chatHistoryMapper;

    @Override
    public void save(String conversationId, String conversationName, ChatTypeEnum chatType) {
        // TODO 获取用户 Id
        String userId = "1";

        Long l = chatHistoryMapper.selectCount(new LambdaQueryWrapper<ChatHistory>()
                .eq(ChatHistory::getUserId, userId)
                .eq(ChatHistory::getConversationId, conversationId)
                .eq(ChatHistory::getChatType, chatType)
                .eq(ChatHistory::getArchive, false)
        );
        if (l > 0) {
            return;
        }

        chatHistoryMapper.insert(ChatHistory.builder()
                .userId(userId)
                .conversationId(conversationId)
                .conversationName(conversationName)
                .chatType(chatType)
                .archive(false)
                .build());

    }

    @Override
    public List<ChatHistoryVO> getChatHistoryList(ChatTypeEnum chatType) {
        // TODO 获取用户 Id
        String userId = "1";
        List<ChatHistory> chatHistories = chatHistoryMapper.selectList(
                new LambdaQueryWrapper<ChatHistory>()
                        .eq(ChatHistory::getChatType, chatType)
                        .eq(ChatHistory::getUserId, userId)
                        .eq(ChatHistory::getArchive, false)
                        .orderByDesc(ChatHistory::getUpdateTime)

        );
        if (CollectionUtils.isEmpty(chatHistories)) {
            return List.of();
        }
        return chatHistories.stream().map(chatHistory -> {
            ChatHistoryVO vo = new ChatHistoryVO();
            BeanUtils.copyProperties(chatHistory, vo);
            return vo;
        }).toList();
    }

    @Override
    public boolean archive(String conversationId) {
        int update = chatHistoryMapper.update(ChatHistory.builder().archive(true).build(),
                new LambdaUpdateWrapper<ChatHistory>()
                        .eq(ChatHistory::getConversationId, conversationId));
        return update > 0;
    }

    @Override
    public List<Message> getChatMessageByConversationId(String conversationId) {
        // TODO 获取用户 Id
        String userId = "1";
        return List.of();
    }
}
