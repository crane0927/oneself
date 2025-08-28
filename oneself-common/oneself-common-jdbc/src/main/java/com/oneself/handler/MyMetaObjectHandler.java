package com.oneself.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.common.handler
 * className MyMetaObjectHandler
 * description MyBatis-Plus 的 MetaObjectHandler 接口的自定义实现
 * version 1.0
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final String USER = "admin";

    /**
     * 在插入实体时，自动填充 `createTime` 和 `updateTime` 字段为当前时间戳。
     *
     * @param metaObject 代表要插入的实体的 metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // TODO 根据实际情况修改创建用户和更新用户的获取方式
        this.setFieldValByName("createBy", USER, metaObject);
        this.setFieldValByName("updateBy", USER, metaObject);
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
        // TODO 根据实际情况修改创建用户和更新用户的获取方式
        this.setFieldValByName("updateBy", USER, metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
