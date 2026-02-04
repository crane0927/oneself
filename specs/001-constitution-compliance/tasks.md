# 任务列表：宪法合规分析与改造

**输入**：设计文档来自 `specs/001-constitution-compliance/`  
**前置**：plan.md、spec.md、research.md、data-model.md、quickstart.md

**测试**：规格未要求测试，故未包含测试类任务。

**组织方式**：任务按用户故事（US1–US4）分组，便于独立实现与验证。

## 格式说明：`[ID] [P?] [Story] 描述`

- **[P]**：可并行执行（不同文件、无依赖）
- **[Story]**：用户故事标签（US1、US2、US3、US4）
- 描述中须包含具体文件路径

## 路径约定

- 仓库根目录：`oneself/`（含 pom.xml 的项目根）
- 模块：`oneself-auth/`、`oneself-common/`、`oneself-gateway/`、`oneself-service/`、`oneself-service-api/`
- 系统服务：`oneself-service/oneself-system/`
- 系统 API：`oneself-service-api/oneself-system-api/`

---

## 阶段 1：准备（共享基础设施）

**目的**：在开展任何合规改造前，确认分支、文档与基线构建。

- [ ] T001 确认分支 001-constitution-compliance 存在，且 specs/001-constitution-compliance/spec.md、plan.md、research.md、data-model.md 存在
- [ ] T002 在仓库根目录（pom.xml 所在处）执行 `mvn clean install` 建立基线；如有既有构建失败须先修复再继续

---

## 阶段 2：基础（阻塞性前置）

**目的**：无需新增共享代码基础设施；本阶段确保基线稳定，以便用户故事阶段可开始。

**⚠️ 关键**：用户故事阶段（3–6）均假定基线构建已通过。

- [ ] T003 确认根 pom.xml 中 Java 版本为 21（原则 1）；否则在 pom.xml 中设置 `<java.version>21</java.version>`

**检查点**：基线构建通过；可进行版本与依赖变更。

---

## 阶段 3：用户故事 1 - 技术栈与版本合规（优先级 P1）🎯 MVP

**目标**：根 POM 与依赖管理与宪法原则 1–3 一致（Java 21、Spring Boot 3.5.9、Spring Cloud 2025.0.1、Spring Cloud Alibaba 2025.0.0.0）；原则 4（PostgreSQL 为主库、MyBatis-Plus、Flyway）得到落实。

**独立验证**：执行 `mvn clean install`；在根 pom.xml 中确认 spring-boot 3.5.9、spring-cloud 2025.0.1、spring-cloud-alibaba 2025.0.0.0；确认已引入 Flyway 且 oneself-system 以 PostgreSQL 为主数据源。

### 用户故事 1 实现任务

- [ ] T004 [US1] 在根 pom.xml 中将 Spring Boot 升级至 3.5.9（parent spring-boot-starter-parent 版本）
- [ ] T005 [US1] 在根 pom.xml 中将 Spring Cloud 升级至 2025.0.1、Spring Cloud Alibaba 升级至 2025.0.0.0（properties 与 dependencyManagement）
- [ ] T006 [US1] 在根 dependencyManagement 中增加 Flyway，在 oneself-service/oneself-system/pom.xml 中增加 flyway 依赖；在 oneself-service/oneself-system/src/main/resources/application.yml（或 application-dev.yml）中为 PostgreSQL 增加 Flyway 配置
- [ ] T007 [US1] 确保 oneself-service/oneself-system 以 PostgreSQL 为主数据源（application-dev.yml / application.yml）；若保留 MySQL，在 README 或 plan 中说明为辅助或迁移路径
- [ ] T008 [US1] 执行 `mvn clean install` 并修复因版本升级导致的依赖或编译问题（gap-001、gap-002、gap-003）

**检查点**：用户故事 1 完成条件为根 POM 版本与宪法一致且构建通过；Flyway 与 PostgreSQL 主库已就绪。

---

## 阶段 4：用户故事 2 - 模块与包结构合规（优先级 P2）

**目标**：模块列表与命名符合宪法原则 11；业务模块包结构符合原则 12（按业务再按分层），或差异已文档化。

**独立验证**：将 oneself-common 下模块名与宪法对照；确认依赖方向无误（无反向依赖）；可选确认 oneself-system 包布局或记录延后重构。

### 用户故事 2 实现任务

- [ ] T009 [US2] 在 specs/001-constitution-compliance/plan.md 或 README 中记录模块与宪法对应关系（common-infra-jdbc 与 infra-db、common-feature 子模块等）；在 Complexity Tracking 中记录已接受的例外
- [ ] T010 [US2] 校验模块依赖方向：service-api 不依赖 infra、无循环依赖；在涉及到的 pom.xml 中修复违规（原则 11–12、依赖矩阵）

**检查点**：用户故事 2 完成条件为文档与依赖检查完成；包结构重构（如 oneself-system 按业务再分层）可延后并记为技术债。

---

## 阶段 5：用户故事 3 - 数据模型与 API 契约合规（优先级 P2）

**目标**：API 模块仅暴露 DTO（无 VO）；Feign 与 Controller 不暴露实体；实体继承规定基类并位于约定的 model 子包（原则 13、16）。

**独立验证**：oneself-service-api 下无 **/model/vo/ 文件；Feign 返回类型为 DTO；oneself-system 实体继承 BasePojo 且位于 model.pojo（或文档化等价关系）。

### 用户故事 3 实现任务

- [ ] T011 [US3] 替换或移除 oneself-service-api/oneself-system-api/src/main/java/com/oneself/model/vo/UserVO.java：在 model/dto 中引入 UserDTO（若不存在），并更新 UserClient 及所有 Feign 调用处使用 DTO；删除或迁出 vo 包（gap-004，原则 13）
- [ ] T012 [US3] 确保 oneself-service/oneself-system/src/main/java/com/oneself/model/pojo/ 下所有实体类继承 oneself-common-infra-jdbc 的 BasePojo；若不重命名，在 plan 或 quickstart 中文档化 BasePojo = BaseEntity 等价（gap-006，原则 16）
- [ ] T013 [US3] 确认无 Controller 或 Feign 接口暴露 Entity 类型；确保 oneself-service/oneself-system 与 oneself-service-api 中响应仅使用 Result/DTO/VO（原则 13，FR-008）

**检查点**：用户故事 3 完成条件为 API 模块无 VO、实体使用基类、且 API 无实体泄露。

---

## 阶段 6：用户故事 4 - 配置、SQL 与部署资产合规（优先级 P3）

**目标**：配置使用 YAML（原则 10）；sql 目录按版本组织并含 README（原则 15）；可运行服务具备 docker/Dockerfile.build 与 docker/Dockerfile.run（原则 17）。

**独立验证**：src/main/resources 下未使用 .properties（或已文档化例外）；oneself-system/sql 含版本子目录与 README；auth、gateway、system 各自具备 docker/Dockerfile.build 与 docker/Dockerfile.run。

### 用户故事 4 实现任务

- [ ] T014 [US4] 确认 oneself-auth、oneself-gateway、oneself-service/oneself-system 下 src/main/resources 中配置均为 YAML；移除或迁移任何 .properties，若有例外在 plan 中说明（原则 10）
- [ ] T015 [US4] 将 oneself-service/oneself-system/sql/ 重构为按版本子目录（如 v1.0.0/），将现有 postgresql.sql/mysql.sql 内容迁入版本脚本；按原则 15 为每版本添加 README 及 sql/README.md（gap-005）
- [ ] T016 [P] [US4] 按原则 17 在 oneself-auth/ 下新增 docker/Dockerfile.build 与 docker/Dockerfile.run（gap-007）
- [ ] T017 [P] [US4] 按原则 17 在 oneself-gateway/ 下新增 docker/Dockerfile.build 与 docker/Dockerfile.run
- [ ] T018 [P] [US4] 按原则 17 在 oneself-service/oneself-system/ 下新增 docker/Dockerfile.build 与 docker/Dockerfile.run

**检查点**：用户故事 4 完成条件为配置、sql 布局与 Docker 资产符合要求或例外已文档化。

---

## 阶段 7：收尾与横切关注

**目的**：文档更新与全量验证。

- [ ] T019 在仓库根目录更新 README.md：写入与宪法一致的技术栈（Java 21、Spring Boot 3.5.9、Spring Cloud 2025.0.1、Alibaba 2025.0.0.0、PostgreSQL 主库、Flyway）及模块概要
- [ ] T020 执行 quickstart.md 验证：运行 `mvn clean install` 并完成 specs/001-constitution-compliance/quickstart.md 中的合规检查清单（目标约 15 分钟）
- [ ] T021 若有未收敛例外，在 specs/001-constitution-compliance/plan.md 的 Complexity Tracking 表中更新：例外项、原因与计划收敛日期

---

## 依赖与执行顺序

### 阶段依赖

- **阶段 1（准备）**：无依赖；最先执行。
- **阶段 2（基础）**：依赖阶段 1；T003 可与 T001–T002 一并完成。
- **阶段 3（US1）**：依赖阶段 2；T004–T008 顺序执行（同属根 pom 与 system 模块）；须先于其他故事完成以避免版本冲突。
- **阶段 4（US2）**：阶段 2 完成后即可开始；可与阶段 3 并行（仅文档与依赖检查）。
- **阶段 5（US3）**：依赖阶段 3（构建稳定）；T011–T013 基本顺序（先改 API 再验证）。
- **阶段 6（US4）**：阶段 2 完成后即可开始；T016–T018 可并行。
- **阶段 7（收尾）**：建议在所有用户故事阶段完成后执行，以体现完整验证。

### 用户故事依赖

- **US1（P1）**：阻塞 US3（API/实体改动前须构建稳定）。与 US2、US4 无依赖。
- **US2（P2）**：独立；仅文档与依赖检查。
- **US3（P2）**：依赖 US1 以得到稳定版本；与 US2、US4 独立。
- **US4（P3）**：与 US1–US3 独立；阶段 2 后可与其它故事并行。

### 并行机会

- T016、T017、T018（auth、gateway、system 的 docker）可并行。
- T009（US2 文档）与 T010（US2 依赖检查）可彼此并行。
- US1 完成后，可由不同开发者并行推进 US2、US4 与 US3。

---

## 实施策略

### MVP 优先（仅用户故事 1）

1. 完成阶段 1 与阶段 2。
2. 完成阶段 3（US1：版本、Flyway、PostgreSQL 主库）。
3. 执行 `mvn clean install` 与 quickstart 合规检查。
4. 暂停并验证；可选合并或继续 US2–US4。

### 增量交付

1. 阶段 1 + 2 → 基线与基础。
2. 阶段 3（US1）→ MVP：技术栈与数据库迁移合规。
3. 阶段 4（US2）→ 模块与文档对齐。
4. 阶段 5（US3）→ API 与实体合规。
5. 阶段 6（US4）→ 配置、SQL、Docker 合规。
6. 阶段 7 → README 与最终检查清单。

### 并行协作策略

- 单人：阶段 1 → 2 → 3 → 5 → 4 → 6 → 7（建议：US1 后 US3，再 US2/US4）。
- 双人：阶段 3 完成后，开发者 A：US2 + US4（T009–T010、T014–T018）；开发者 B：US3（T011–T013）。

---

## 说明

- 带 [P] 的任务对应不同模块/文件，无交叉依赖。
- [USn] 用于将任务映射到用户故事，便于追溯。
- 各用户故事可通过规格中的独立验证条件与上文检查点独立验证。
- 建议每任务或每逻辑组提交一次；在 T004–T005 后及 T011 后执行 `mvn clean install` 以尽早发现回归。
- 若原则 12 的包结构重构（按业务再分层）延后，请在 plan 的 Complexity Tracking 中记录并注明收敛计划。
