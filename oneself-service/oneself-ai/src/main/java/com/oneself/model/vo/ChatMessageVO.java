package com.oneself.model.vo;

import com.oneself.model.pojo.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.MessageType;

import java.util.Map;

/**
 * @author liuhuan
 * date 2025/4/18
 * packageName com.oneself.model.vo
 * className ChatMessageVO
 * description
 * version 1.0
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ChatMessageVO extends AbstractMessage {

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "角色类型")
    private MessageType role;

    public ChatMessageVO(MessageType role, String message) {
        super(role, message, Map.of());
        this.role = role;
        this.message = message;
    }

    public static ChatMessageVO from(ChatMessage chatMessage) {
        return new ChatMessageVO(chatMessage.getRole(), chatMessage.getMessage());
    }
}