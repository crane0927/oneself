package com.oneself.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.model.enums.ChatTypeEnum;
import lombok.*;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.model.pojo
 * className ChatHistory
 * description 会话历史表
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("chat_history")
public class ChatHistory extends BasePojo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(value = "user_id")
    private String userId;
    @TableField(value = "chat_type")
    private ChatTypeEnum chatType;
    @TableField(value = "conversation_id")
    private String conversationId;
    @TableField(value = "conversation_name")
    private String conversationName;
    @TableField(value = "archive")
    private Boolean archive;
}
