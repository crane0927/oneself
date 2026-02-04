# Research: 宪法合规改造

**Feature**: 001-constitution-compliance  
**Date**: 2026-02-04

本文档汇总 Phase 0 研究结论，用于消除 Technical Context 中的不确定性并支撑 Phase 1 设计。

---

## 1. 技术栈版本升级（原则 1–3）

**Decision**: 将根 POM 中 Spring Boot 从 3.4.4 升级为 3.5.9，Spring Cloud 从 2024.0.1 升级为 2025.0.1，Spring Cloud Alibaba 从 2023.0.3.2 升级为 2025.0.0.0。

**Rationale**:
- 宪法明确要求上述版本以与 JDK 21 及生态兼容、统一团队环境。
- Spring Boot 3.5.x 与 3.4.x 同属 3.x 主线，BOM 升级后需验证依赖兼容性（如 Nacos、MyBatis-Plus、Knife4j 等）；若有破坏性变更，在任务中单独处理。

**Alternatives considered**:
- 保持当前版本并申请宪法例外：不被采纳，因规格要求技术栈与宪法一致为首要目标。
- 仅升级 Spring Boot 不升级 Cloud：宪法要求 BOM 统一管理，需同步升级。

---

## 2. 主库与数据库迁移工具（原则 4）

**Decision**: 以 PostgreSQL 为唯一主关系型数据库；引入 Flyway 或 Liquibase 管理迁移脚本；现有 MySQL 配置作为辅助或迁移过渡时保留并文档化，不作为主库。

**Rationale**:
- 宪法规定主库为 PostgreSQL，迁移工具为 Flyway 或 Liquibase。当前项目已有 postgresql.sql，且支持多数据源；合规路径为明确主库并统一迁移方式。
- Flyway 与 Spring Boot 集成简单、脚本即 SQL 文件，与现有 `sql/` 目录易于结合；Liquibase 支持多格式与回滚。二选一即可。

**Alternatives considered**:
- 继续双主库（MySQL + PostgreSQL）：与宪法“主库唯一”冲突，不采纳。
- 仅文档化不引入 Flyway/Liquibase：宪法验证项要求迁移由工具管理，需落实。

**Decision (细化)**: 优先选用 **Flyway**；将 `oneself-system/sql/` 调整为按版本子目录（如 `v1.0.0/`），Flyway 可引用该目录或与 `db/migration` 并存，以宪法原则 15 的目录要求为准。

---

## 3. 实体基类与模块命名（原则 16、原则 11）

**Decision**: 将 `oneself-common-infra-jdbc` 中的 `BasePojo` 视为宪法中 `BaseEntity` 的等效实现；包路径保持 `model.pojo` 或按宪法改为 `model.entity` 二选一，由任务阶段统一。模块名 `infra-jdbc` 与宪法 `infra-db` 语义一致，可保留 `infra-jdbc` 并在宪法合规检查清单中注明“与 principle 16 对应模块为 common-infra-jdbc”，或重命名为 `oneself-common-infra-db` 以与宪法措辞完全一致。

**Rationale**:
- 规格假设允许“基类命名与模块命名若与宪法用词不同但语义一致，可视为已合规或通过最小重命名达到合规”。当前 BasePojo 已提供审计与逻辑删除字段，与宪法 BaseEntity 职责一致。
- 若团队希望与宪法文本完全一致，可采用重命名：BasePojo → BaseEntity，infra-jdbc → infra-db；否则在合规清单中记录等价关系即可。

**Alternatives considered**:
- 在 common 下新增 common-infra-db 并复制 BaseEntity：重复代码，不采纳。
- 强制重命名：采纳为可选任务，与“最小改动”平衡后由任务列表决定是否在本期执行。

---

## 4. API 模块中的 VO（原则 13）

**Decision**: 从 `oneself-system-api` 中移除 `model.vo` 包下的类型（如 UserVO）；Feign 与对外契约仅使用 DTO。原 UserVO 若被其他服务或前端依赖，迁移为 DTO（如 UserDTO）并在 system-api 中暴露，业务服务或前端如需“视图形态”在各自模块内定义 VO 并基于 DTO 转换。

**Rationale**:
- 宪法禁止在 API 模块中定义 VO，仅允许 DTO 及枚举等。当前 system-api 存在 `model/vo/UserVO.java`，属于明确违规，必须改造。

**Alternatives considered**:
- 将 UserVO 改名为 UserDTO 保留在 API：若字段与用途确为“服务间传输”，可重命名为 UserDTO 并放入 model.dto；若包含前端展示专用字段，应迁出到调用方或业务模块的 VO。

---

## 5. SQL 目录与版本化（原则 15）

**Decision**: 涉库服务（如 oneself-system）的 `sql/` 目录下按版本建子目录（如 `v1.0.0/`），每版本内放该版本变更脚本及 README 说明变更目的与顺序；可与 Flyway 的 `db/migration` 并存（Flyway 引用或复制脚本），或仅作文档与手写执行用，以宪法“按版本记录”为准。

**Rationale**:
- 宪法要求 sql 目录存在且按版本组织、有说明；当前仅有顶层 mysql.sql/postgresql.sql，需重构为版本子目录并补充 README。

**Alternatives considered**:
- 仅保留单层 sql 文件：不符合原则 15，不采纳。

---

## 6. Dockerfile 布局（原则 17）

**Decision**: 为可独立启动的微服务（oneself-auth、oneself-gateway、oneself-system）增加 `docker/` 目录，内含 `Dockerfile.build`（构建阶段）与 `Dockerfile.run`（运行阶段）；或保留根目录单 Dockerfile 但文档化为“例外”并约定收敛时间。推荐落实两阶段 Dockerfile 以符合宪法。

**Rationale**:
- 宪法要求编译/运行分离、轻量运行镜像；当前各服务仅根目录单 Dockerfile，需补齐或登记例外。

**Alternatives considered**:
- 维持单 Dockerfile 并申请长期例外：仅在短期无法投入时使用，并需在 plan 的 Complexity Tracking 中记录收敛计划。

---

## 7. 包结构按业务分层（原则 12）

**Decision**: 业务服务（如 oneself-system）的包结构当前为扁平（com.oneself.controller、com.oneself.service 等）。宪法要求业务模块“按业务再按技术分层”（如 com.oneself.system.user.controller）。本期可列为 P2 改造：先完成技术栈与 API 契约合规，再按子域（user、role、dept 等）拆分包；若改动量过大，可在首次合规中记录为技术债并设后续迭代完成。

**Rationale**:
- 包结构重构面广，易引入合并冲突与回归；与规格“分阶段、保证构建通过”一致的做法是优先做版本与契约，包结构可分批做。

**Alternatives considered**:
- 本期不碰包结构：可接受，但需在合规清单中标记“原则 12 待办”。
- 本期全量重包名：风险高，建议分步。

---

## 总结

| 主题 | 结论 | 备注 |
|------|------|------|
| 技术栈版本 | 升级至 SB 3.5.9、SC 2025.0.1、Alibaba 2025.0.0.0 | 需验证依赖兼容 |
| 主库与迁移 | PostgreSQL 为主；引入 Flyway；SQL 按版本目录 | MySQL 可作过渡/辅助并文档化 |
| 实体基类/模块 | BasePojo 等同 BaseEntity；infra-jdbc 等同 infra-db，可选重命名 | 规格允许语义等效 |
| API 模块 VO | 移除或迁出 VO，仅保留 DTO | UserVO → DTO 或迁出 |
| SQL 目录 | 按版本子目录 + README | 与 Flyway 可并存 |
| Dockerfile | 增加 docker/Dockerfile.build + Dockerfile.run | 或登记例外与收敛时间 |
| 包结构 | P2 可分批按业务分层 | 可标为技术债 |

以上决策在 Phase 1 的 data-model 与任务拆分中具体化为改造项与验收条件。
