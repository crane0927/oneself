

-- 枚举类型定义
CREATE TYPE dept_status AS ENUM ('NORMAL', 'LOCKED');
CREATE TYPE user_sex AS ENUM ('UNKNOWN', 'MALE', 'FEMALE');
CREATE TYPE user_type AS ENUM ('ADMIN', 'NORMAL');
CREATE TYPE user_status AS ENUM ('NORMAL', 'LOCKED');

-- 部门表
CREATE TABLE dept (
                      id          BIGSERIAL PRIMARY KEY,
                      dept_name   VARCHAR(255) NOT NULL,
                      dept_desc   VARCHAR(255),
                      sequence    INT NOT NULL,
                      parent_id   BIGINT NOT NULL,
                      leader      VARCHAR(255) NOT NULL,
                      phone       VARCHAR(50) NOT NULL,
                      email       VARCHAR(255) NOT NULL,
                      status      dept_status NOT NULL DEFAULT 'NORMAL',
                      deleted     SMALLINT NOT NULL DEFAULT 0,
                      create_by   VARCHAR(255) NOT NULL,
                      create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      update_by   VARCHAR(255) NOT NULL,
                      update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- dept 表触发器函数
CREATE OR REPLACE FUNCTION dept_before_insert()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.sequence IS NULL THEN
SELECT COALESCE(MAX(sequence), 0) + 1
INTO NEW.sequence
FROM dept;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- dept 表触发器
CREATE TRIGGER dept_before_insert_trigger
    BEFORE INSERT ON dept
    FOR EACH ROW
    EXECUTE FUNCTION dept_before_insert();

-- 用户表
CREATE TABLE "user" (
                        id              BIGSERIAL PRIMARY KEY,
                        dept_id         BIGINT NOT NULL REFERENCES dept(id),
                        login_name      VARCHAR(255) NOT NULL,
                        username        VARCHAR(255) NOT NULL,
                        password        VARCHAR(255) NOT NULL,
                        sex             user_sex NOT NULL DEFAULT 'UNKNOWN',
                        user_type       user_type NOT NULL DEFAULT 'NORMAL',
                        status          user_status NOT NULL DEFAULT 'NORMAL',
                        avatar          VARCHAR(255),
                        email           VARCHAR(255),
                        phone           VARCHAR(255),
                        last_login_ip   VARCHAR(50),
                        last_login_time TIMESTAMP,
                        remark          TEXT,
                        deleted         SMALLINT NOT NULL DEFAULT 0,
                        create_by       VARCHAR(255) NOT NULL,
                        create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        update_by       VARCHAR(255) NOT NULL,
                        update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO oneself_system.dept (id, dept_name, dept_desc, sequence, parent_id, leader, phone, email, status, deleted, create_by, create_time, update_by, update_time) VALUES (1, 'oneself', null, 1, 0, 'crane', '13300000000', 'crane0927@oneself.com', 'NORMAL', 0, 'admin', '2025-09-01 11:05:22', 'admin', '2025-09-01 11:05:22');
