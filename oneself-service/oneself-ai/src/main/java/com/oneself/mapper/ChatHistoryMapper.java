package com.oneself.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.model.pojo.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/4/17
 * packageName com.oneself.mapper
 * interfaceName ChatHistoryMapper
 * description 会话历史表映射
 * version 1.0
 */
@Mapper
@Repository
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
}
