# Implementation Plan: 宪法合规分析与改造

**Branch**: `001-constitution-compliance` | **Date**: 2026-02-04 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `specs/001-constitution-compliance/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command.

## Summary

对当前代码库进行宪法合规分析，识别与 `.specify/memory/constitution.md` 的差距，并分阶段实施改造：优先技术栈与版本（P1）、模块与包结构（P2）、数据模型与 API 契约（P2）、配置/SQL/部署资产（P3）。改造过程保持可构建、可测试、可部署；例外项需文档化并约定收敛时间。

## Technical Context

**Language/Version**: Java 21（宪法原则 1）  
**Primary Dependencies**: Spring Boot 3.5.9（目标）、Spring Cloud 2025.0.1、Spring Cloud Alibaba 2025.0.0.0、MyBatis-Plus、Nacos（宪法原则 2–5）  
**Storage**: PostgreSQL 为主库（宪法原则 4）；Redis/MongoDB/Elasticsearch 仅作辅助；数据库迁移由 Flyway 或 Liquibase 管理  
**Testing**: Maven Surefire（单元测试）；全量构建与现有自动化测试作为回归门禁  
**Target Platform**: JVM 21，Linux 服务器部署，Docker 镜像构建（原则 17）  
**Project Type**: 多模块 Maven 项目（oneself-auth、oneself-common、oneself-gateway、oneself-service、oneself-service-api）  
**Performance Goals**: 改造不引入性能回退；构建与合规检查在合理时间内完成（如 SC-004：合规核对约 15 分钟内）  
**Constraints**: 改造分阶段提交、每步保持构建通过；不破坏现有 API 契约（仅内部类型与结构变更）  
**Scale/Scope**: 全仓库模块与配置；重点为根 POM、common 子模块、system 服务与 system-api、SQL 与 Docker 资产

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

本特性为宪法合规改造，实施过程中必须遵守宪法，改造结果须满足以下门禁：

| 原则 | 门禁条件 | 改造后状态 |
|------|----------|------------|
| 1 | Java 21 在根 POM 显式声明 | 必须通过 |
| 2–3 | Spring Boot 3.5.9、Spring Cloud 2025.0.1、Spring Cloud Alibaba 2025.0.0.0 由父 POM 管理 | 必须通过 |
| 4 | 主库 PostgreSQL、MyBatis-Plus、Flyway/Liquibase | 必须通过（或文档化例外与收敛计划） |
| 10 | 配置文件 YAML，禁止仅用 .properties | 必须通过 |
| 11–12 | 模块划分与包结构符合宪法 11–12 | 必须通过或合理例外 |
| 13 | API 模块仅 DTO；Controller/Feign 不暴露 Entity | 必须通过 |
| 15 | 涉库服务 sql 目录按版本组织 + 说明 | 必须通过 |
| 16 | 实体继承规定基类（BaseEntity/等效）并位于 model 约定子包 | 必须通过或语义等效 |
| 17 | 可运行微服务具备编译/运行两阶段 Dockerfile 或等效 | 必须通过或文档化例外 |

**Gate 结果**: 无豁免；改造目标即满足上述门禁。若某条原则暂以“例外”保留，必须在 plan/README 中记录原因与收敛时间。

## Project Structure

### Documentation (this feature)

```text
specs/001-constitution-compliance/
├── plan.md              # 本文件
├── research.md          # Phase 0 研究与决策
├── data-model.md        # 合规差距与改造项模型
├── quickstart.md        # 合规验证与构建指引
├── contracts/           # 本特性无新增 API，见 contracts/README.md
├── checklists/
│   └── requirements.md  # 规格质量检查清单
└── tasks.md             # Phase 2 由 /speckit.tasks 生成
```

### Source Code (repository root)

```text
oneself/
├── pom.xml                                    # 根 POM：版本与依赖管理
├── oneself-auth/                              # 认证服务
│   ├── Dockerfile                             # 当前单文件 → 原则 17 需 docker/Dockerfile.build + Dockerfile.run
│   ├── pom.xml
│   └── src/main/{java,resources}/
├── oneself-common/                            # 公共模块
│   ├── oneself-common-core/
│   ├── oneself-common-infra/
│   │   ├── oneself-common-infra-jdbc/         # 宪法为 infra-db；含 BasePojo → 原则 16 对齐 BaseEntity
│   │   ├── oneself-common-infra-redis/
│   │   ├── oneself-common-infra-mongodb/
│   │   └── oneself-common-infra-elasticsearch/
│   └── oneself-common-feature/
│       ├── oneself-common-feature-security/
│       ├── oneself-common-feature-sensitive/
│       ├── oneself-common-feature-swagger/
│       └── oneself-common-feature-web/
├── oneself-gateway/
│   ├── Dockerfile
│   └── src/main/{java,resources}/
├── oneself-service/
│   └── oneself-system/
│       ├── Dockerfile
│       ├── sql/                               # 当前 mysql.sql + postgresql.sql → 原则 15 按版本子目录
│       └── src/main/java/com/oneself/
│           ├── controller/                    # 原则 12：业务模块建议按业务再分层（如 user.controller）
│           ├── mapper/
│           ├── model/{dto,pojo,vo}/           # 原则 13/16：pojo 与宪法 entity 对齐；API 仅 DTO
│           └── service/
└── oneself-service-api/
    └── oneself-system-api/
        └── src/main/java/com/oneself/
            ├── client/
            └── model/
                ├── enums/
                └── vo/                        # 原则 13 禁止：API 模块仅 DTO，需移除或迁出 VO
```

**Structure Decision**: 多模块 Maven 微服务；改造不改变顶层模块列表，仅调整版本、依赖、包路径、SQL 与 Docker 布局以符合宪法。

## Complexity Tracking

> 本特性为合规改造，无新增架构复杂度；若某条原则暂不满足并列为例外，在此记录。

**模块与宪法对应（原则 11）**：当前 common 子模块命名与宪法略有差异，语义等价：`oneself-common-infra-jdbc` 对应宪法中的 infra-db（数据库基础设施），`oneself-common-feature-*` 与宪法一致。包结构按业务分层（原则 12）可延后为技术债，在后续迭代中按子域拆分。

| 例外（若存在） | 原因 | 收敛计划 |
|----------------|------|----------|
| 构建需 JDK 21 或 Lombok 1.18.38+ | 使用 JDK 24 编译时 Lombok 与 javac TypeTag 兼容性；根 POM 已使用 Lombok 1.18.38 | 保持根 POM lombok.version≥1.18.38；推荐用 JDK 21 执行构建 |
| service-api 经 common-feature-security 传递依赖 infra-redis | 宪法要求 service-api 不直接依赖 infra；当前仅为传递依赖 | 已确认无直接 infra 依赖；传递依赖可接受，必要时可拆 security 的 redis 为可选 |
