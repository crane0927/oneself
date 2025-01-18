CREATE
DATABASE `oneself-system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */

create table dept
(
    id          bigint auto_increment comment '部门 ID'
        primary key,
    dept_name   varchar(255)                                        not null comment '部门名称',
    dept_desc   varchar(255)                                        null comment '部门描述',
    sequence    int                                                 not null comment '排序',
    parent_id   bigint                                              not null comment '父部门 ID',
    leader      varchar(255)                                        not null comment '负责人',
    phone       varchar(50)                                         not null comment '联系电话',
    email       varchar(255)                                        not null comment '邮箱',
    status      enum ('NORMAL', 'LOCKED') default 'NORMAL'          not null comment '状态（NORMAL:正常, LOCKED:锁定）',
    deleted     tinyint                   default 0                 not null comment '逻辑删除标志（0:未删除, 1:已删除）',
    create_by   varchar(255)                                        not null comment '创建者',
    create_time datetime                  default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(255)                                        not null comment '更新者',
    update_time datetime                  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '部门表';

create definer = liuhuan@`%` trigger dept_before_insert
    before insert
    on dept
    for each row
BEGIN
  IF NEW.sequence IS NULL THEN
    SET NEW.sequence = (SELECT IFNULL(MAX(sequence), 0) + 1 FROM `oneself-system`.dept);
END IF;
END;

create table user
(
    id              bigint auto_increment comment '用户 ID'
        primary key,
    dept_id         bigint                                                       not null comment '部门 ID',
    login_name      varchar(255)                                                 not null comment '登录名',
    username        varchar(255)                                                 not null comment '用户名',
    password        varchar(255)                                                 not null comment '密码',
    sex             enum ('UNKNOWN', 'MALE', 'FEMALE') default 'UNKNOWN'         not null comment '性别（UNKNOWN:未知, MALE:男, FEMALE:女）',
    user_type       enum ('ADMIN', 'NORMAL')           default 'NORMAL'          not null comment '用户类型（ADMIN:管理员, NORMAL:普通用户）',
    status          enum ('NORMAL', 'LOCKED')          default 'NORMAL'          not null comment '状态（NORMAL:正常, LOCKED:锁定）',
    avatar          varchar(255)                                                 null comment '头像',
    email           varchar(255)                                                 null comment '邮箱',
    phone           varchar(255)                                                 null comment '手机号',
    last_login_ip   varchar(50)                                                  null comment '最后登录 IP',
    last_login_time timestamp                                                    null comment '最后登录时间',
    remark          text                                                         null comment '备注',
    deleted         tinyint                            default 0                 not null comment '逻辑删除标志（0:未删除, 1:已删除）',
    create_by       varchar(255)                                                 not null comment '创建者',
    create_time     datetime                           default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by       varchar(255)                                                 not null comment '更新者',
    update_time     datetime                           default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户表';

