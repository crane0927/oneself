-------------------------------
-- 表名: dept (部门表)
-- 功能: 存储部门组织结构信息
-------------------------------
CREATE TABLE dept
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- 主键ID
    name        VARCHAR(100) NOT NULL,                      -- 部门名称
    parent_id   UUID,                                       -- 父部门ID
    sort_order  INTEGER          DEFAULT 0,                 -- 排序序号
    status      INTEGER          DEFAULT 0,                 -- 状态(0-禁用,1-启用)
    deleted     INTEGER          DEFAULT 0,                 -- 逻辑删除标志(0-未删除,1-已删除)
    create_by   VARCHAR(100),                               -- 创建人
    create_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_by   VARCHAR(100),                               -- 更新人
    update_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

COMMENT
ON TABLE dept IS '部门表';
COMMENT
ON COLUMN dept.id IS '主键ID';
COMMENT
ON COLUMN dept.name IS '部门名称';
COMMENT
ON COLUMN dept.parent_id IS '父部门ID';
COMMENT
ON COLUMN dept.sort_order IS '排序序号';
COMMENT
ON COLUMN dept.status IS '状态(0-禁用,1-启用)';
COMMENT
ON COLUMN dept.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT
ON COLUMN dept.create_by IS '创建人';
COMMENT
ON COLUMN dept.create_time IS '创建时间';
COMMENT
ON COLUMN dept.update_by IS '更新人';
COMMENT
ON COLUMN dept.update_time IS '更新时间';

-------------------------------
-- 表名: role (角色表)
-- 功能: 存储系统角色信息
-------------------------------
CREATE TABLE role
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- 主键ID
    role_code   VARCHAR(100) NOT NULL UNIQUE,               -- 角色编码
    role_name   VARCHAR(100) NOT NULL,                      -- 角色名称
    description TEXT,                                       -- 角色描述
    status      INTEGER          DEFAULT 0,                 -- 状态(0-禁用,1-启用)
    deleted     INTEGER          DEFAULT 0,                 -- 逻辑删除标志(0-未删除,1-已删除)
    create_by   VARCHAR(100),                               -- 创建人
    create_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_by   VARCHAR(100),                               -- 更新人
    update_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

COMMENT
ON TABLE role IS '角色表';
COMMENT
ON COLUMN role.id IS '主键ID';
COMMENT
ON COLUMN role.role_code IS '角色编码';
COMMENT
ON COLUMN role.role_name IS '角色名称';
COMMENT
ON COLUMN role.description IS '角色描述';
COMMENT
ON COLUMN role.status IS '状态(0-禁用,1-启用)';
COMMENT
ON COLUMN role.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT
ON COLUMN role.create_by IS '创建人';
COMMENT
ON COLUMN role.create_time IS '创建时间';
COMMENT
ON COLUMN role.update_by IS '更新人';
COMMENT
ON COLUMN role.update_time IS '更新时间';

-------------------------------
-- 表名: permission (权限表)
-- 功能: 存储系统权限资源信息
-------------------------------
CREATE TABLE permission
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- 主键ID
    perm_code     VARCHAR(100) NOT NULL UNIQUE,               -- 权限编码
    perm_name     VARCHAR(100) NOT NULL,                      -- 权限名称
    description   TEXT,                                       -- 权限描述
    resource_type VARCHAR(50),                                -- 资源类型(menu-菜单,button-按钮,api-接口)
    resource_path VARCHAR(255),                               -- 资源路径
    parent_id     UUID,                                       -- 父权限ID
    sort_order    INTEGER          DEFAULT 0,                 -- 排序序号
    status        INTEGER          DEFAULT 0,                 -- 状态(0-禁用,1-启用)
    deleted       INTEGER          DEFAULT 0,                 -- 逻辑删除标志(0-未删除,1-已删除)
    create_by     VARCHAR(100),                               -- 创建人
    create_time   TIMESTAMP        DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_by     VARCHAR(100),                               -- 更新人
    update_time   TIMESTAMP        DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

COMMENT
ON TABLE permission IS '权限表';
COMMENT
ON COLUMN permission.id IS '主键ID';
COMMENT
ON COLUMN permission.perm_code IS '权限编码';
COMMENT
ON COLUMN permission.perm_name IS '权限名称';
COMMENT
ON COLUMN permission.description IS '权限描述';
COMMENT
ON COLUMN permission.resource_type IS '资源类型(menu-菜单,button-按钮,api-接口)';
COMMENT
ON COLUMN permission.resource_path IS '资源路径';
COMMENT
ON COLUMN permission.parent_id IS '父权限ID';
COMMENT
ON COLUMN permission.sort_order IS '排序序号';
COMMENT
ON COLUMN permission.status IS '状态(0-禁用,1-启用)';
COMMENT
ON COLUMN permission.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT
ON COLUMN permission.create_by IS '创建人';
COMMENT
ON COLUMN permission.create_time IS '创建时间';
COMMENT
ON COLUMN permission.update_by IS '更新人';
COMMENT
ON COLUMN permission.update_time IS '更新时间';

-------------------------------
-- 表名: user_role (用户角色关联表)
-- 功能: 存储用户与角色的多对多关系
-------------------------------
CREATE TABLE user_role
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- 主键ID
    user_id     UUID NOT NULL,                              -- 用户ID
    role_id     UUID NOT NULL,                              -- 角色ID
    deleted     INTEGER          DEFAULT 0,                 -- 逻辑删除标志(0-未删除,1-已删除)
    create_by   VARCHAR(100),                               -- 创建人
    create_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_by   VARCHAR(100),                               -- 更新人
    update_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

COMMENT
ON TABLE user_role IS '用户角色关联表';
COMMENT
ON COLUMN user_role.id IS '主键ID';
COMMENT
ON COLUMN user_role.user_id IS '用户ID';
COMMENT
ON COLUMN user_role.role_id IS '角色ID';
COMMENT
ON COLUMN user_role.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT
ON COLUMN user_role.create_by IS '创建人';
COMMENT
ON COLUMN user_role.create_time IS '创建时间';
COMMENT
ON COLUMN user_role.update_by IS '更新人';
COMMENT
ON COLUMN user_role.update_time IS '更新时间';

-------------------------------
-- 表名: role_permission (角色权限关联表)
-- 功能: 存储角色与权限的多对多关系
-------------------------------
CREATE TABLE role_permission
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- 主键ID
    role_id     UUID NOT NULL,                              -- 角色ID
    perm_id     UUID NOT NULL,                              -- 权限ID
    deleted     INTEGER          DEFAULT 0,                 -- 逻辑删除标志(0-未删除,1-已删除)
    create_by   VARCHAR(100),                               -- 创建人
    create_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_by   VARCHAR(100),                               -- 更新人
    update_time TIMESTAMP        DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

COMMENT
ON TABLE role_permission IS '角色权限关联表';
COMMENT
ON COLUMN role_permission.id IS '主键ID';
COMMENT
ON COLUMN role_permission.role_id IS '角色ID';
COMMENT
ON COLUMN role_permission.perm_id IS '权限ID';
COMMENT
ON COLUMN role_permission.deleted IS '逻辑删除标志(0-未删除,1-已删除)';
COMMENT
ON COLUMN role_permission.create_by IS '创建人';
COMMENT
ON COLUMN role_permission.create_time IS '创建时间';
COMMENT
ON COLUMN role_permission.update_by IS '更新人';
COMMENT
ON COLUMN role_permission.update_time IS '更新时间';

-- 创建索引以提高查询性能
CREATE INDEX idx_user_username ON "user" (username);
CREATE INDEX idx_user_dept_id ON "user" (dept_id);
CREATE INDEX idx_user_status ON "user" (status);
CREATE INDEX idx_user_sex ON "user" (sex);
CREATE INDEX idx_user_type ON "user" (type);
CREATE INDEX idx_user_deleted ON "user" (deleted);

CREATE INDEX idx_role_role_code ON role (role_code);
CREATE INDEX idx_role_status ON role (status);
CREATE INDEX idx_role_deleted ON role (deleted);

CREATE INDEX idx_permission_perm_code ON permission (perm_code);
CREATE INDEX idx_permission_parent_id ON permission (parent_id);
CREATE INDEX idx_permission_resource_type ON permission (resource_type);
CREATE INDEX idx_permission_status ON permission (status);
CREATE INDEX idx_permission_deleted ON permission (deleted);

CREATE INDEX idx_user_role_user_id ON user_role (user_id);
CREATE INDEX idx_user_role_role_id ON user_role (role_id);
CREATE INDEX idx_user_role_deleted ON user_role (deleted);

CREATE INDEX idx_role_permission_role_id ON role_permission (role_id);
CREATE INDEX idx_role_permission_perm_id ON role_permission (perm_id);
CREATE INDEX idx_role_permission_deleted ON role_permission (deleted);

CREATE INDEX idx_dept_parent_id ON dept (parent_id);
CREATE INDEX idx_dept_status ON dept (status);
CREATE INDEX idx_dept_deleted ON dept (deleted);