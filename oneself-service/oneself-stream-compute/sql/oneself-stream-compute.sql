CREATE
DATABASE `oneself-stream-compute` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */

create table kafka_cluster
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    name              varchar(255)                                 not null comment '集群名称',
    description       text                                         null comment '描述信息',
    bootstrap_servers varchar(500)                                not null comment 'Kafka服务器地址列表',
    kafka_version     varchar(50)                                 null comment 'Kafka版本',
    enable_auth       tinyint(1) default 0                        null comment '是否启用认证 (0-否，1-是)',
    security_protocol enum('PLAINTEXT', 'SASL_PLAINTEXT', 'SASL_SSL', 'SSL') not null comment '安全协议类型 (KafkaSecurityProtocolEnum)',
    username          varchar(255)                                null comment '认证用户名',
    password          varchar(255)                                null comment '认证密码',
    status            enum('DISABLED', 'ENABLED') default 'ENABLED' not null comment '集群状态 (DISABLED:禁用, ENABLED:启用)',
    deleted           tinyint(1) default 0                        not null comment '逻辑删除标志 (0:未删除，1:已删除)',
    create_by         varchar(64)                                 null comment '创建人',
    create_time       datetime default CURRENT_TIMESTAMP          not null comment '创建时间',
    update_by         varchar(64)                                 null comment '更新人',
    update_time       datetime default CURRENT_TIMESTAMP          not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment 'Kafka集群配置表' collate = utf8mb4_unicode_ci;

