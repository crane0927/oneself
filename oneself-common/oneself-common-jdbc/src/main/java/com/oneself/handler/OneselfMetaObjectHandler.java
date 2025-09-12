package com.oneself.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.oneself.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.common.handler
 * className OneselfMetaObjectHandler
 * description MyBatis-Plus 的 MetaObjectHandler 接口的自定义实现
 * version 1.0
 */
@Component
@RequiredArgsConstructor
public class OneselfMetaObjectHandler implements MetaObjectHandler {

    private final SecurityUtils securityUtils;

    /**
     * 在插入实体时，自动填充 `createTime` 和 `updateTime` 字段为当前时间戳。
     *
     * @param metaObject 代表要插入的实体的 metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createBy", securityUtils.getCurrentUsername(), metaObject);
        this.setFieldValByName("updateBy", securityUtils.getCurrentUsername(), metaObject);
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

    /**
     * 在更新实体时，自动填充 `updateTime` 字段为当前时间戳。
     *
     * @param metaObject 代表要更新的实体的 metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateBy", securityUtils.getCurrentUsername(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
