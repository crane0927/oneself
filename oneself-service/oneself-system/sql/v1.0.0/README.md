# SQL 版本 v1.0.0

- **版本号**: v1.0.0
- **变更日期**: 2026-02-04
- **变更说明**: 初始 oneself-system 主库 PostgreSQL 表结构（部门、用户、角色、权限、用户角色、角色权限、约束配置）及索引。
- **执行顺序**: 001_init_schema_postgresql.sql
- **回滚**: 按表逆序 DROP TABLE（sys_constraint → sys_role_permission → sys_user_role → sys_permission → sys_role → sys_user → sys_dept）。
