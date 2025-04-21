package com.oneself.service;

import com.alibaba.nacos.shaded.com.google.protobuf.Message;
import com.oneself.model.enums.ChatTypeEnum;
import com.oneself.model.vo.ChatHistoryVO;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.service
 * interfaceName ChatHistoryService
 * description 会话历史接口
 * version 1.0
 */
public interface ChatHistoryService {

    /**
     * 保存会话历史
     *
     * @param conversationId   会话 ID
     * @param conversationName 会话名称
     * @param chatType         会话类型
     */
    void save(String conversationId, String conversationName, ChatTypeEnum chatType);

    /**
     * 根据会话类型获取历史列表
     *
     * @param chatType 会话类型
     * @return 历史列表
     */
    List<ChatHistoryVO> getChatHistoryList(ChatTypeEnum chatType);

    /**
     * 归档会话
     *
     * @param conversationId 会话 ID
     * @return 归档结果
     */
    boolean archive(String conversationId);

    List<Message> getChatMessageByConversationId(String conversationId);
}
