# Feature Specification: 宪法合规分析与改造

**Feature Branch**: `001-constitution-compliance`  
**Created**: 2026-02-04  
**Status**: Draft  
**Input**: User description: "分析当前项目，哪些地方不符合宪法规范，进行改造"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - 技术栈与版本合规 (Priority: P1)

作为项目维护者，我希望项目使用的技术栈及版本与项目宪法一致，以便构建、依赖和协作环境统一，降低兼容性风险。

**Why this priority**: 技术栈是后续所有改造的前提，版本不一致会导致依赖冲突和文档与实现脱节。

**Independent Test**: 通过检查根 POM 与各模块 POM 中的版本声明，确认与宪法规定一致；构建通过即可验证。

**Acceptance Scenarios**:

1. **Given** 项目根 POM 存在，**When** 检查 Java 与主框架版本，**Then** 与宪法原则 1–3 规定的版本一致（如 Java 21、指定 Spring Boot / Spring Cloud 版本）。
2. **Given** 项目使用关系型数据库，**When** 检查主数据源与持久化方式，**Then** 符合宪法原则 4（主库选型、数据访问方式、迁移工具要求）。
3. **Given** 项目已构建，**When** 执行完整构建与测试，**Then** 无因版本或依赖违反宪法而导致的构建/测试失败。

---

### User Story 2 - 模块与包结构合规 (Priority: P2)

作为开发人员，我希望模块划分和包命名符合宪法中的模块化与包结构规范，以便快速定位代码、保持依赖方向清晰。

**Why this priority**: 模块与包结构影响日常开发效率和架构边界，应在技术栈合规之后、业务对象规范之前落地。

**Independent Test**: 通过目录与包名检查、依赖关系检查，确认模块列表与包命名符合宪法原则 11–12。

**Acceptance Scenarios**:

1. **Given** 宪法规定的标准模块结构，**When** 对比当前仓库模块列表与命名，**Then** 差异已记录并在合理范围内或已改造（如 common 子模块命名与职责划分）。
2. **Given** 业务服务模块，**When** 检查包结构，**Then** 符合“按业务再按技术分层”或“按技术再按业务”的规则（原则 12）。
3. **Given** 各模块，**When** 检查模块间依赖方向，**Then** 无违反宪法模块职责与依赖矩阵的依赖。

---

### User Story 3 - 数据模型与 API 契约合规 (Priority: P2)

作为接口消费者与提供方，我希望 API 模块仅暴露 DTO、服务层与控制器正确使用 DTO/VO/Entity，以便契约清晰、前后端与服务间调用不混淆。

**Why this priority**: DTO/VO/Entity 混用会导致接口不稳定和重复定义，影响多端协作。

**Independent Test**: 通过代码与包扫描，确认 API 模块无 VO、业务模块 model 分层与命名符合宪法原则 13；实体继承符合原则 16。

**Acceptance Scenarios**:

1. **Given** 任一 service-api 模块，**When** 检查其对外暴露的类型，**Then** 仅包含 DTO（及枚举等宪法允许的类型），不包含 VO。
2. **Given** 业务服务模块中的持久化实体，**When** 检查基类与包路径，**Then** 继承宪法规定的基类（如 BaseEntity/统一审计基类），且位于规定的 model 子包（如 entity/pojo 与宪法约定一致）。
3. **Given** Controller 与 Feign 接口，**When** 检查入参与返回值类型，**Then** 不直接暴露持久化实体，使用 DTO/VO 与统一响应包装。

---

### User Story 4 - 配置、SQL 与部署资产合规 (Priority: P3)

作为运维与发布负责人，我希望配置文件格式、SQL 脚本组织方式、可运行服务的镜像构建方式符合宪法，以便环境一致、变更可追溯、部署可重复。

**Why this priority**: 配置与 SQL、Dockerfile 的规范影响发布安全与可维护性，可在核心代码与契约合规之后收尾。

**Independent Test**: 检查 resource 下配置格式、各库相关服务的 sql 目录结构、可运行服务的 Docker 相关文件，符合原则 10、15、17。

**Acceptance Scenarios**:

1. **Given** 各模块 resource 目录，**When** 检查配置文件格式，**Then** 符合宪法原则 10（如优先 YAML，禁止仅用 .properties 等）。
2. **Given** 涉及数据库的服务，**When** 检查 sql 目录，**Then** 存在按版本组织的 SQL 目录（及必要说明），符合原则 15。
3. **Given** 可独立启动的微服务，**When** 检查镜像构建资产，**Then** 符合原则 17（如具备编译/运行两阶段 Dockerfile 或与宪法约定等效的构建方式）。

---

### Edge Cases

- 若宪法某条原则与当前技术债或历史约束冲突，改造范围应明确记录为“例外”并注明计划收敛时间，避免无限期偏离。
- 若某模块同时需要多种数据库（如主库 + 缓存），仅主关系型库与访问方式需符合原则 4；其余按“辅助存储”处理并文档化。
- 包结构改造可能涉及大量重命名与引用更新，需保证构建与测试通过后再合并，必要时分批次提交。

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: 项目 MUST 使用宪法原则 1 规定的 Java 版本，并在构建配置中显式声明。
- **FR-002**: 项目 MUST 使用宪法原则 2–3 规定的 Spring Boot、Spring Cloud、Spring Cloud Alibaba 版本（或经修订程序批准的替代版本），并由父 POM 统一管理。
- **FR-003**: 主关系型数据库选型、数据访问方式与迁移工具 MUST 符合宪法原则 4（如主库为指定类型、使用指定持久化框架、迁移脚本由指定工具管理）。
- **FR-004**: 模块划分与命名 MUST 符合宪法原则 11；包结构 MUST 符合原则 12（按业务/技术分层约定）。
- **FR-005**: 各模块依赖关系 MUST 符合宪法中的模块职责与依赖矩阵，禁止反向或循环依赖。
- **FR-006**: API 模块（service-api）MUST 仅暴露 DTO 及宪法允许的类型，禁止在 API 模块中定义或暴露 VO。
- **FR-007**: 业务服务中持久化实体 MUST 继承宪法原则 16 规定的基类，并位于规定的 model 子包中。
- **FR-008**: Controller 与 Feign 接口 MUST 不直接暴露持久化实体；对外响应 MUST 使用统一包装与 DTO/VO。
- **FR-009**: 配置文件格式 MUST 符合宪法原则 10；涉及数据库的服务的 SQL 目录与版本管理 MUST 符合原则 15。
- **FR-010**: 可独立启动的微服务 MUST 具备符合宪法原则 17 的构建/运行资产（或文档化例外与收敛计划）。

### Key Entities *(include if feature involves data)*

- **宪法（Constitution）**: 项目根治理文档，定义技术栈、模块、包、DTO/VO/Entity、SQL、Docker 等原则与验证方式。
- **合规差距（Gap）**: 当前代码库或配置与某条宪法原则的不一致项，含位置、原则编号、建议改造方向。
- **改造项（Remediation Item）**: 针对某一合规差距的具体改动集合（如版本升级、重命名、移动文件、新增配置或脚本）。

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 所有在本次改造范围内列出的宪法原则，均有对应的“通过/例外”结论；例外项均有文档与计划收敛时间。
- **SC-002**: 全量构建与自动化测试通过，无因本次合规改造引入的回归失败。
- **SC-003**: 新成员仅凭宪法与 README 即可理解技术栈版本、模块职责与包约定，并在一次代码导航内定位到正确模块与包。
- **SC-004**: 合规审查清单（或等价检查项）可被重复执行，并在 15 分钟内完成对技术栈、模块、API 契约、SQL 与 Docker 的合规性核对。

## Assumptions

- 宪法文档（.specify/memory/constitution.md）为唯一权威来源；若与根目录 constitution.md 不一致，以 .specify/memory 为准，差异可另行同步。
- 改造优先保证“可构建、可测试、可部署”，再逐步收敛到与宪法完全一致；允许分阶段提交与合并。
- 现有多数据源（如同时支持 MySQL/PostgreSQL）若与宪法“主库唯一”冲突，以宪法为准确定主库，其余作为辅助或迁移路径需在文档中说明。
- 实体基类命名（如 BaseEntity vs BasePojo）与模块命名（如 common-infra-db vs common-infra-jdbc）若与宪法用词不同但语义一致，可视为已合规或通过最小重命名/别名达到合规。
