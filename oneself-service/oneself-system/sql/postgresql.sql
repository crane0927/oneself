-------------------------------
-- 表名: sys_dept (部门表)
-- 功能: 存储部门组织结构信息
-------------------------------
CREATE TABLE sys_dept (
                          id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''), -- 主键ID
                          name VARCHAR(100) NOT NULL,                             -- 部门名称
                          parent_id VARCHAR(32),                                  -- 父部门ID
                          sort_order INTEGER DEFAULT 0,                           -- 排序序号
                          status INTEGER DEFAULT 0,                               -- 状态(0-禁用,1-启用)
                          deleted INTEGER DEFAULT 0,                              -- 逻辑删除标志(0-未删除,1-已删除)
                          create_by VARCHAR(100),                                 -- 创建人
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
                          update_by VARCHAR(100),                                 -- 更新人
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- 更新时间
);

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '主键ID';
COMMENT ON COLUMN sys_dept.name IS '部门名称';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.sort_order IS '排序序号';
COMMENT ON COLUMN sys_dept.status IS '状态(0-禁用,1-启用)';
COMMENT ON COLUMN sys_dept.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT ON COLUMN sys_dept.create_by IS '创建人';
COMMENT ON COLUMN sys_dept.create_time IS '创建时间';
COMMENT ON COLUMN sys_dept.update_by IS '更新人';
COMMENT ON COLUMN sys_dept.update_time IS '更新时间';

-------------------------------
-- 表名: sys_user (用户表)
-- 功能: 存储系统用户信息
-------------------------------
CREATE TABLE sys_user (
                          id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''), -- 主键ID
                          username VARCHAR(100) NOT NULL UNIQUE,                  -- 用户名
                          password VARCHAR(255) NOT NULL,                         -- 密码
                          email VARCHAR(100),                                     -- 邮箱
                          phone VARCHAR(20),                                      -- 手机号
                          real_name VARCHAR(100),                                 -- 真实姓名
                          sex INTEGER DEFAULT 0,                                  -- 性别(0-未知,1-男,2-女)
                          type INTEGER DEFAULT 1,                                 -- 用户类型(0-管理员,1-普通用户)
                          dept_id VARCHAR(32),                                    -- 所属部门ID
                          status INTEGER DEFAULT 0,                               -- 状态(0-禁用,1-启用)
                          deleted INTEGER DEFAULT 0,                              -- 逻辑删除标志(0-未删除,1-已删除)
                          create_by VARCHAR(100),                                 -- 创建人
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
                          update_by VARCHAR(100),                                 -- 更新人
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- 更新时间
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '主键ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.real_name IS '真实姓名';
COMMENT ON COLUMN sys_user.sex IS '性别(0-未知,1-男,2-女)';
COMMENT ON COLUMN sys_user.type IS '用户类型(0-管理员,1-普通用户)';
COMMENT ON COLUMN sys_user.dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user.status IS '状态(0-禁用,1-启用)';
COMMENT ON COLUMN sys_user.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT ON COLUMN sys_user.create_by IS '创建人';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_by IS '更新人';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';

-------------------------------
-- 表名: sys_role (角色表)
-- 功能: 存储系统角色信息
-------------------------------
CREATE TABLE sys_role (
                          id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''), -- 主键ID
                          role_code VARCHAR(100) NOT NULL UNIQUE,                 -- 角色编码
                          role_name VARCHAR(100) NOT NULL,                        -- 角色名称
                          description TEXT,                                       -- 角色描述
                          status INTEGER DEFAULT 0,                               -- 状态(0-禁用,1-启用)
                          deleted INTEGER DEFAULT 0,                              -- 逻辑删除标志(0-未删除,1-已删除)
                          create_by VARCHAR(100),                                 -- 创建人
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
                          update_by VARCHAR(100),                                 -- 更新人
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- 更新时间
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '主键ID';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.description IS '角色描述';
COMMENT ON COLUMN sys_role.status IS '状态(0-禁用,1-启用)';
COMMENT ON COLUMN sys_role.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT ON COLUMN sys_role.create_by IS '创建人';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_by IS '更新人';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';

-------------------------------
-- 表名: sys_permission (权限表)
-- 功能: 存储系统权限资源信息
-------------------------------
CREATE TABLE sys_permission (
                                id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''), -- 主键ID
                                perm_code VARCHAR(100) NOT NULL UNIQUE,                 -- 权限编码
                                perm_name VARCHAR(100) NOT NULL,                        -- 权限名称
                                description TEXT,                                       -- 权限描述
                                resource_type VARCHAR(50),                              -- 资源类型(menu-菜单,button-按钮,api-接口)
                                resource_path VARCHAR(255),                             -- 资源路径
                                parent_id VARCHAR(32),                                  -- 父权限ID
                                sort_order INTEGER DEFAULT 0,                           -- 排序序号
                                status INTEGER DEFAULT 0,                               -- 状态(0-禁用,1-启用)
                                deleted INTEGER DEFAULT 0,                              -- 逻辑删除标志(0-未删除,1-已删除)
                                create_by VARCHAR(100),                                 -- 创建人
                                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
                                update_by VARCHAR(100),                                 -- 更新人
                                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- 更新时间
);

COMMENT ON TABLE sys_permission IS '权限表';
COMMENT ON COLUMN sys_permission.id IS '主键ID';
COMMENT ON COLUMN sys_permission.perm_code IS '权限编码';
COMMENT ON COLUMN sys_permission.perm_name IS '权限名称';
COMMENT ON COLUMN sys_permission.description IS '权限描述';
COMMENT ON COLUMN sys_permission.resource_type IS '资源类型(menu-菜单,button-按钮,api-接口)';
COMMENT ON COLUMN sys_permission.resource_path IS '资源路径';
COMMENT ON COLUMN sys_permission.parent_id IS '父权限ID';
COMMENT ON COLUMN sys_permission.sort_order IS '排序序号';
COMMENT ON COLUMN sys_permission.status IS '状态(0-禁用,1-启用)';
COMMENT ON COLUMN sys_permission.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT ON COLUMN sys_permission.create_by IS '创建人';
COMMENT ON COLUMN sys_permission.create_time IS '创建时间';
COMMENT ON COLUMN sys_permission.update_by IS '更新人';
COMMENT ON COLUMN sys_permission.update_time IS '更新时间';

-------------------------------
-- 表名: sys_user_role (用户角色关联表)
-- 功能: 存储用户与角色的多对多关系
-------------------------------
CREATE TABLE sys_user_role (
                               id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''), -- 主键ID
                               user_id VARCHAR(32) NOT NULL,                           -- 用户ID
                               role_id VARCHAR(32) NOT NULL,                           -- 角色ID
                               deleted INTEGER DEFAULT 0,                              -- 逻辑删除标志(0-未删除,1-已删除)
                               create_by VARCHAR(100),                                 -- 创建人
                               create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
                               update_by VARCHAR(100),                                 -- 更新人
                               update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- 更新时间
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.id IS '主键ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT ON COLUMN sys_user_role.create_by IS '创建人';
COMMENT ON COLUMN sys_user_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_user_role.update_by IS '更新人';
COMMENT ON COLUMN sys_user_role.update_time IS '更新时间';

-------------------------------
-- 表名: sys_role_permission (角色权限关联表)
-- 功能: 存储角色与权限的多对多关系
-------------------------------
CREATE TABLE sys_role_permission (
                                     id VARCHAR(32) PRIMARY KEY DEFAULT REPLACE(gen_random_uuid()::text, '-', ''), -- 主键ID
                                     role_id VARCHAR(32) NOT NULL,                           -- 角色ID
                                     perm_id VARCHAR(32) NOT NULL,                           -- 权限ID
                                     deleted INTEGER DEFAULT 0,                              -- 逻辑删除标志(0-未删除,1-已删除)
                                     create_by VARCHAR(100),                                 -- 创建人
                                     create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
                                     update_by VARCHAR(100),                                 -- 更新人
                                     update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- 更新时间
);

COMMENT ON TABLE sys_role_permission IS '角色权限关联表';
COMMENT ON COLUMN sys_role_permission.id IS '主键ID';
COMMENT ON COLUMN sys_role_permission.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_permission.perm_id IS '权限ID';
COMMENT ON COLUMN sys_role_permission.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT ON COLUMN sys_role_permission.create_by IS '创建人';
COMMENT ON COLUMN sys_role_permission.create_time IS '创建时间';
COMMENT ON COLUMN sys_role_permission.update_by IS '更新人';
COMMENT ON COLUMN sys_role_permission.update_time IS '更新时间';

-- 创建索引以提高查询性能
CREATE INDEX idx_sys_user_username ON sys_user(username);
CREATE INDEX idx_sys_user_dept_id ON sys_user(dept_id);
CREATE INDEX idx_sys_user_status ON sys_user(status);
CREATE INDEX idx_sys_user_sex ON sys_user(sex);
CREATE INDEX idx_sys_user_type ON sys_user(type);
CREATE INDEX idx_sys_user_deleted ON sys_user(deleted);

CREATE INDEX idx_sys_role_role_code ON sys_role(role_code);
CREATE INDEX idx_sys_role_status ON sys_role(status);
CREATE INDEX idx_sys_role_deleted ON sys_role(deleted);

CREATE INDEX idx_sys_permission_perm_code ON sys_permission(perm_code);
CREATE INDEX idx_sys_permission_parent_id ON sys_permission(parent_id);
CREATE INDEX idx_sys_permission_resource_type ON sys_permission(resource_type);
CREATE INDEX idx_sys_permission_status ON sys_permission(status);
CREATE INDEX idx_sys_permission_deleted ON sys_permission(deleted);

CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);
CREATE INDEX idx_sys_user_role_deleted ON sys_user_role(deleted);

CREATE INDEX idx_sys_role_permission_role_id ON sys_role_permission(role_id);
CREATE INDEX idx_sys_role_permission_perm_id ON sys_role_permission(perm_id);
CREATE INDEX idx_sys_role_permission_deleted ON sys_role_permission(deleted);

CREATE INDEX idx_sys_dept_parent_id ON sys_dept(parent_id);
CREATE INDEX idx_sys_dept_status ON sys_dept(status);
CREATE INDEX idx_sys_dept_deleted ON sys_dept(deleted);