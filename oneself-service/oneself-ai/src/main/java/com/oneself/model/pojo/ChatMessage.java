package com.oneself.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.springframework.ai.chat.messages.MessageType;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.pojo
 * className ChatMessage
 * description  会话消息表
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("chat_message")
public class ChatMessage extends BasePojo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(value = "user_id", fill = FieldFill.INSERT)
    private String userId;
    @TableField(value = "conversation_id")
    private String conversationId;
    @TableField(value = "message")
    private String message;
    @TableField(value = "role")
    private MessageType role;
}
