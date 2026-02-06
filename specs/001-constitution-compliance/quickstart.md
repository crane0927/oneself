# Quickstart: 宪法合规验证与构建

**Feature**: 001-constitution-compliance  
**Date**: 2026-02-04

本文档说明如何在本地验证宪法合规状态并执行全量构建，供开发与审查使用。

---

## 前置条件

- JDK 21
- Maven 3.9+
- （可选）Docker，用于验证镜像构建

---

## 1. 克隆与分支

```bash
git clone <repo-url>
cd oneself
git checkout 001-constitution-compliance
```

---

## 2. 全量构建

```bash
mvn clean install -DskipTests   # 首次可先跳过测试以快速验证编译
mvn clean install               # 完整构建含测试
```

改造过程中每一步提交前应保证上述命令通过，满足 SC-002。

---

## 3. 宪法合规快速核对（约 15 分钟内）

按以下清单逐项检查，与 `.specify/memory/constitution.md` 对照：

| 类别 | 检查项 | 命令/方式 |
|------|--------|-----------|
| 原则 1 | Java 21 | `grep -r java.version pom.xml` → 21 |
| 原则 2–3 | Spring Boot / Cloud 版本 | 打开根 `pom.xml` 查看 parent 与 dependencyManagement |
| 原则 4 | 主库 PostgreSQL、MyBatis-Plus、迁移工具 | 检查根及 system 模块依赖与配置；sql 目录与 Flyway 配置 |
| 原则 10 | 配置为 YAML | `find . -name "*.properties" -not -path "./target/*"` 应为空或已弃用 |
| 原则 11–12 | 模块与包结构 | 查看各模块包名是否符合“业务按业务分层、技术按技术分层” |
| 原则 13 | API 模块无 VO | `find oneself-service-api -name "*.java" -path "*/vo/*"` 应为空 |
| 原则 15 | sql 按版本目录 | 涉库服务下 `sql/v*` 存在且含 README |
| 原则 16 | 实体继承基类 | 业务实体继承 BasePojo/BaseEntity，且位于 model 约定子包；当前项目使用 oneself-common-infra-jdbc 的 BasePojo，与宪法中的 BaseEntity 语义等价 |
| 原则 17 | Dockerfile 两阶段 | 可运行服务存在 `docker/Dockerfile.build` 与 `docker/Dockerfile.run` |

可将上述清单保存为脚本或 CI 步骤，实现 SC-004（可重复执行、约 15 分钟内完成）。

---

## 4. 单模块构建与运行（可选）

```bash
# 以 system 服务为例
cd oneself-service/oneself-system
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

确认启动无报错、依赖与配置符合宪法（如数据源、Nacos 等）。

---

## 5. Docker 构建验证（原则 17，改造完成后）

```bash
cd oneself-auth   # 或 gateway、oneself-system
docker build -f docker/Dockerfile.build -t oneself-auth:build .
# 运行阶段镜像构建依具体 Dockerfile.run 而定
```

---

## 参考

- 规格与成功标准: [spec.md](../spec.md)  
- 实施计划与门禁: [plan.md](../plan.md)  
- 宪法全文: [.specify/memory/constitution.md](../../.specify/memory/constitution.md)
