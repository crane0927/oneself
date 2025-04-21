package com.oneself.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.model.pojo.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.mapper
 * interfaceName ChatMessageMapper
 * description 会话消息表映射
 * version 1.0
 */
@Mapper
@Repository
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
