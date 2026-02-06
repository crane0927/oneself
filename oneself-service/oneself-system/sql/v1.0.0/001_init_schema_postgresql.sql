-- Version: v1.0.0
-- 用途: oneself-system 主库 PostgreSQL 初始化表结构（宪法原则 15）
-- 执行顺序: 001

-------------------------------
-- 表名: sys_dept (部门表)
-------------------------------
CREATE TABLE sys_dept (
                          id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''),
                          dept_name VARCHAR(100) NOT NULL,
                          parent_id VARCHAR(32),
                          sort_order INTEGER DEFAULT 0,
                          status INTEGER DEFAULT 0,
                          deleted INTEGER DEFAULT 0,
                          create_by VARCHAR(100),
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_by VARCHAR(100),
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE sys_dept IS '部门表';

-------------------------------
-- 表名: sys_user (用户表)
-------------------------------
CREATE TABLE sys_user (
                          id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''),
                          username VARCHAR(100) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          email VARCHAR(100),
                          phone VARCHAR(20),
                          real_name VARCHAR(100),
                          sex INTEGER DEFAULT 0,
                          type INTEGER DEFAULT 1,
                          dept_id VARCHAR(32),
                          status INTEGER DEFAULT 0,
                          deleted INTEGER DEFAULT 0,
                          create_by VARCHAR(100),
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_by VARCHAR(100),
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE sys_user IS '用户表';

-------------------------------
-- 表名: sys_role (角色表)
-------------------------------
CREATE TABLE sys_role (
                          id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''),
                          role_code VARCHAR(100) NOT NULL UNIQUE,
                          role_name VARCHAR(100) NOT NULL,
                          description TEXT,
                          parent_id VARCHAR(32),
                          status INTEGER DEFAULT 0,
                          deleted INTEGER DEFAULT 0,
                          create_by VARCHAR(100),
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_by VARCHAR(100),
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE sys_role IS '角色表';

-------------------------------
-- 表名: sys_permission (权限表)
-------------------------------
CREATE TABLE sys_permission (
                                id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''),
                                perm_code VARCHAR(100) NOT NULL UNIQUE,
                                perm_name VARCHAR(100) NOT NULL,
                                description TEXT,
                                resource_type VARCHAR(50),
                                resource_path VARCHAR(255),
                                parent_id VARCHAR(32),
                                sort_order INTEGER DEFAULT 0,
                                status INTEGER DEFAULT 0,
                                deleted INTEGER DEFAULT 0,
                                create_by VARCHAR(100),
                                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                update_by VARCHAR(100),
                                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE sys_permission IS '权限表';

-------------------------------
-- 表名: sys_user_role (用户角色关联表)
-------------------------------
CREATE TABLE sys_user_role (
                               id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''),
                               user_id VARCHAR(32) NOT NULL,
                               role_id VARCHAR(32) NOT NULL,
                               deleted INTEGER DEFAULT 0,
                               create_by VARCHAR(100),
                               create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               update_by VARCHAR(100),
                               update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE sys_user_role IS '用户角色关联表';

-------------------------------
-- 表名: sys_role_permission (角色权限关联表)
-------------------------------
CREATE TABLE sys_role_permission (
                                     id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''),
                                     role_id VARCHAR(32) NOT NULL,
                                     perm_id VARCHAR(32) NOT NULL,
                                     deleted INTEGER DEFAULT 0,
                                     create_by VARCHAR(100),
                                     create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     update_by VARCHAR(100),
                                     update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE sys_role_permission IS '角色权限关联表';

-------------------------------
-- 表名: sys_constraint (约束配置表)
-------------------------------
CREATE TABLE sys_constraint (
                                id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''),
                                constraint_type VARCHAR(50) NOT NULL,
                                constraint_name VARCHAR(100) NOT NULL,
                                constraint_value TEXT,
                                description TEXT,
                                status INTEGER DEFAULT 1,
                                deleted INTEGER DEFAULT 0,
                                create_by VARCHAR(100),
                                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                update_by VARCHAR(100),
                                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE sys_constraint IS '约束配置表（RBAC2）';

-- 索引
CREATE INDEX idx_sys_user_username ON sys_user(username);
CREATE INDEX idx_sys_user_dept_id ON sys_user(dept_id);
CREATE INDEX idx_sys_user_status ON sys_user(status);
CREATE INDEX idx_sys_role_role_code ON sys_role(role_code);
CREATE INDEX idx_sys_permission_perm_code ON sys_permission(perm_code);
CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);
CREATE INDEX idx_sys_role_permission_role_id ON sys_role_permission(role_id);
CREATE INDEX idx_sys_role_permission_perm_id ON sys_role_permission(perm_id);
CREATE INDEX idx_sys_dept_parent_id ON sys_dept(parent_id);
CREATE INDEX idx_sys_constraint_type ON sys_constraint(constraint_type);
