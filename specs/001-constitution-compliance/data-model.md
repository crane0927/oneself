# Data Model: 宪法合规改造

**Feature**: 001-constitution-compliance  
**Date**: 2026-02-04

本特性不引入新的业务实体表，而是对“合规差距”与“改造项”进行建模，用于任务拆分与验收。

---

## 概念实体

### 1. 宪法（Constitution）

| 属性 | 说明 |
|------|------|
| 来源 | `.specify/memory/constitution.md` |
| 原则列表 | 原则 1–21（技术栈、架构、项目结构、质量、治理） |
| 验证方式 | 各原则下的“验证”清单 |

本改造以该文档为唯一权威；不修改宪法内容，仅使代码库与配置符合其规定。

---

### 2. 合规差距（Gap）

表示当前仓库与某一条宪法原则的不一致。

| 属性 | 类型 | 说明 |
|------|------|------|
| id | 唯一标识 | 如 gap-001 |
| principle | 原则编号 | 如 2、4、13 |
| location | 位置 | 文件/模块/配置路径 |
| currentState | 文本 | 当前状态简述 |
| targetState | 文本 | 宪法要求的状态 |
| priority | P1/P2/P3 | 与 spec 用户故事优先级一致 |

**示例**:

| id | principle | location | currentState | targetState | priority |
|----|-----------|-----------|--------------|-------------|----------|
| gap-001 | 2 | 根 pom.xml | spring-boot 3.4.4 | 3.5.9 | P1 |
| gap-002 | 3 | 根 pom.xml | spring-cloud 2024.0.1, alibaba 2023.0.3.2 | 2025.0.1, 2025.0.0.0 | P1 |
| gap-003 | 4 | 根 pom / system | 多数据源、无 Flyway | PostgreSQL 为主 + Flyway/Liquibase | P1 |
| gap-004 | 13 | oneself-system-api/model/vo | 存在 UserVO | API 模块仅 DTO | P2 |
| gap-005 | 15 | oneself-system/sql | 仅 mysql.sql、postgresql.sql | 按版本子目录 + README | P3 |
| gap-006 | 16 | common-infra-jdbc, system pojo | BasePojo, model.pojo | BaseEntity 或等效，model.entity 或约定 | P2 |
| gap-007 | 17 | auth/gateway/system 根目录 | 单 Dockerfile | docker/Dockerfile.build + Dockerfile.run | P3 |

---

### 3. 改造项（Remediation Item）

针对一个或多个合规差距的具体改动动作。

| 属性 | 类型 | 说明 |
|------|------|------|
| id | 唯一标识 | 如 rem-001 |
| gapIds | 引用 | 对应的 Gap id 列表 |
| action | 枚举/文本 | 如 version-upgrade, remove-file, add-directory, rename, refactor-package |
| scope | 位置 | 影响的模块/文件 |
| acceptance | 文本 | 验收条件（可测试） |
| dependency | 引用 | 依赖的其他改造项 id（可选） |

**关系**: 一个 Gap 可对应多个 Remediation Item（例如原则 15 对应“创建版本目录”+“添加 README”）；一个 Remediation Item 可解决多个 Gap（例如“升级根 POM 版本”同时满足原则 2 与 3）。

---

## 状态与流程

- **Gap**: 待处理 → 已分配改造项 → 已验收  
- **Remediation Item**: 待实施 → 进行中 → 已合并 → 已验收  

验收标准与 spec 中 FR/SC 一致：构建通过、宪法检查清单逐项通过或例外已文档化。

---

## 与任务（tasks.md）的对应

Phase 2 生成的 `tasks.md` 将把上述 Remediation Item 拆解为具体任务（T001、T002…），并标注与 Gap/原则的对应关系，便于追踪与代码审查。
