package com.oneself.model.vo;

import com.oneself.annotation.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.model.vo
 * className ChatHistoryVO
 * description 会话历史 VO
 * version 1.0
 */
@Data
public class ChatHistoryVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "会话 ID")
    private String conversationId;
    @Schema(description = "会话名称")
    private String conversationName;
}
