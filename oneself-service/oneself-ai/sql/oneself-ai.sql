CREATE
DATABASE `oneself_ai` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */

create table chat_history
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    user_id           varchar(255) not null comment '用户ID',
    chat_type         varchar(50)  not null comment '聊天类型（枚举）',
    conversation_id   varchar(255) not null comment '会话ID',
    conversation_name varchar(255) null comment '会话名称',
    archive           tinyint(1) default 0                 null comment '是否归档',
    deleted           tinyint(1) default 0                 null comment '逻辑删除标志（0 未删除，1 已删除）',
    create_by         varchar(64) null comment '创建人',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by         varchar(64) null comment '更新人',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '聊天记录表';

CREATE TABLE `chat_message`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         VARCHAR(255) NOT NULL COMMENT '用户ID',
    `conversation_id` VARCHAR(255) NOT NULL COMMENT '会话ID',
    `message`         TEXT         NOT NULL COMMENT '消息内容',
    `role`            VARCHAR(50)  NOT NULL COMMENT '消息角色（用户/助手）',
    `deleted`         TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志（0 未删除，1 已删除）',
    `create_by`       VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `create_time`     DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `update_time`     DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';
