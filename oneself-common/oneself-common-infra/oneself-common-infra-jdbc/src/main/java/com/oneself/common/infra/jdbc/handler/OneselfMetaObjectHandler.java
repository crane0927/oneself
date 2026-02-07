package com.oneself.common.infra.jdbc.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.oneself.common.core.provider.CurrentUserProvider;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.common.handler
 * className OneselfMetaObjectHandler
 * description MyBatis-Plus 的 MetaObjectHandler 接口的自定义实现
 * version 1.0
 */
@Component
public class OneselfMetaObjectHandler implements MetaObjectHandler {

    /**
     * 当前用户提供者，可选依赖
     * 如果 common-security 模块存在，会自动注入实现
     * 如果不存在，则使用默认值 "system"
     */
    private final CurrentUserProvider currentUserProvider;

    private static final String DEFAULT_USER = "system";

    /**
     * 构造函数注入，支持可选依赖
     *
     * @param currentUserProvider 当前用户提供者，如果不存在则为 null
     */
    @Autowired(required = false)
    public OneselfMetaObjectHandler(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * 获取当前用户名，如果 CurrentUserProvider 不存在则返回默认值
     *
     * @return 当前用户名
     */
    private String getCurrentUsername() {
        return Optional.ofNullable(currentUserProvider)
                .map(CurrentUserProvider::getCurrentUsername)
                .orElse(DEFAULT_USER);
    }

    /**
     * 在插入实体时，自动填充 `createTime` 和 `updateTime` 字段为当前时间戳。
     *
     * @param metaObject 代表要插入的实体的 metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        String username = getCurrentUsername();
        this.setFieldValByName("createBy", username, metaObject);
        this.setFieldValByName("updateBy", username, metaObject);
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
        String username = getCurrentUsername();
        this.setFieldValByName("updateBy", username, metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
