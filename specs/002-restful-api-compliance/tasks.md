# Tasks: 现有接口 RESTful 宪法符合性检查与整改

**输入**：设计文档来自 `specs/002-restful-api-compliance/`  
**前置**：plan.md、spec.md、research.md、data-model.md、contracts/、quickstart.md

**测试**：规格未要求 TDD 或独立测试任务，本任务列表不包含测试任务。

**组织方式**：按用户故事分组，便于独立实施与验收。

## 格式：`[ID] [P?] [Story?] 描述`

- **[P]**: 可并行（不同文件、无依赖）
- **[Story]**: 所属用户故事（US1, US2, US3）
- 描述中包含具体文件或目录路径

---

## Phase 1：准备（共享）

**目的**：为审计与报告准备基础文档与占位文件

- [x] T001 按 data-model.md 在 specs/002-restful-api-compliance/checklists/principle-6-checklist.md 编写原则 6 检查清单文档（C1–C6）
- [x] T002 [P] 在 specs/002-restful-api-compliance/audit-report.md（或按 contracts/audit-report.schema.json 的 audit-report.json）创建审计报告模板并说明输出格式

---

## Phase 2：基础（阻塞性前置）

**目的**：产出完整接口清单，后续审计与整改均依赖此清单

**⚠️ 必须**：未完成本阶段不得开始用户故事 1

- [x] T003 扫描 oneself-auth、oneself-system 的 Controller 与 oneself-system-api 的 Feign 客户端，在 specs/002-restful-api-compliance/endpoint-inventory.md 中列出模块、路径与 HTTP 方法，产出接口清单

**检查点**：接口清单就绪，可开始 US1 审计

---

## Phase 3：用户故事 1 - 生成接口符合性审计报告 (P1) 🎯 MVP

**目标**：交付一份完整的原则 6 符合性审计报告，覆盖所有对外接口，每条规则有通过/不通过/例外及建议。

**独立验收**：执行审计流程后可查阅 audit-report，覆盖 endpoint-inventory 中全部接口，C1–C6 每项均有结论；任一不符合项附原因与建议改法。

### 用户故事 1 实施任务

- [x] T004 [US1] 对 specs/002-restful-api-compliance/endpoint-inventory.md 中每个接口，按 specs/002-restful-api-compliance/checklists/principle-6-checklist.md 评估 C1–C6，在 specs/002-restful-api-compliance/audit-report.md（或按 contracts/audit-report.schema.json 的 JSON）中记录通过/不通过/例外及原因与建议
- [x] T005 [US1] 在 specs/002-restful-api-compliance/audit-report.md 中添加汇总段（通过/不通过/例外数量），并将认证接口（/login、/logout、/refresh、/captcha）标注为原则 6 例外并说明理由

**检查点**：用户故事 1 完成，可独立交付审计报告并用于 US2 整改

---

## Phase 4：用户故事 2 - 按审计结果整改不符合项 (P2)

**目标**：根据审计报告对不符合项逐项整改，使接口满足原则 6；涉及破坏性变更时采用版本化或兼容策略；API 文档同步更新；复检通过。

**独立验收**：报告中每条「不通过」均有对应改动；复检报告通过率 100%（已批准例外除外）；API 文档与实现一致并标注方法与状态码。

### 用户故事 2 实施任务

- [x] T006 [US2] 按审计报告整改 oneself-auth 不符合项（已文档化例外除外），修改 oneself-auth/src/main/java/com/oneself/controller/AuthController.java
- [x] T007 [US2] 按报告整改 oneself-system 用户相关接口与 Feign（如 get/user/by、list/by/dept、分页等），修改 oneself-service/oneself-system/src/main/java/com/oneself/controller/UserController.java 与 oneself-service-api/oneself-system-api/src/main/java/com/oneself/client/UserClient.java
- [ ] T008 [US2] 按报告整改 oneself-system 角色、部门、权限、配置、约束 Controller，修改 oneself-service/oneself-system/src/main/java/com/oneself/controller/ 下对应类
- [ ] T009 [US2] 按报告整改 oneself-system 用户角色与角色权限 Controller，修改 oneself-service/oneself-system/src/main/java/com/oneself/controller/UserRoleController.java 与 RolePermissionController.java
- [ ] T010 [US2] 为所有变更接口更新 API 文档（Swagger/OpenAPI 或 README），按 FR-005 标注 HTTP 方法与主要状态码
- [x] T011 [US2] 重新执行审计并将复检报告保存到 specs/002-restful-api-compliance/audit-report-recheck.md，确认除已文档化例外外 100% 通过

**检查点**：用户故事 2 完成，所有接口符合原则 6 或已记录例外，复检报告可查

---

## Phase 5：用户故事 3 - 将符合性纳入持续验证 (P3)

**目标**：建立原则 6 的持续验证机制，使新增或修改接口在合入前经过检查；不符合项在合入前被纠正或经例外审批并记录。

**独立验收**：存在可执行的检查步骤（MR 清单或 CI）；新增故意违反原则 6 的接口时，流程能发现并阻止或告警。

### 用户故事 3 实施任务

- [x] T012 [P] [US3] 在 specs/002-restful-api-compliance/checklists/principle-6-mr-checklist.md 编写原则 6 的 MR 代码评审检查清单
- [x] T013 [P] [US3] 按 research.md 在 scripts/ 或 .specify/scripts/ 添加可选 CI 脚本或记录脚本位置，用于扫描 Controller 路径中的动词或非标准分页
- [x] T014 [US3] 在 specs/002-restful-api-compliance/quickstart.md（或 specs 目录下 README）中说明例外审批流程与 MR 门禁

**检查点**：用户故事 3 完成，持续验证机制可执行

---

## Phase 6：收尾与交叉检查

**目的**：全量构建与 quickstart 验证，确保无回退

- [x] T015 [P] 在仓库根目录执行全量构建（mvn -q clean compile 或 test）并修复任何回归
- [x] T016 按 quickstart.md 步骤验证（审计 → 整改 → 复检），视需更新 specs/002-restful-api-compliance/quickstart.md

---

## 依赖与执行顺序

### 阶段依赖

- **Phase 1（准备）**：无依赖，可立即开始
- **Phase 2（基础）**：依赖 Phase 1 完成；**阻塞所有用户故事**
- **Phase 3（US1）**：依赖 Phase 2 完成
- **Phase 4（US2）**：依赖 Phase 3 完成（需审计报告）
- **Phase 5（US3）**：依赖 Phase 4 完成（整改后再上持续验证）
- **Phase 6（收尾）**：依赖 Phase 4 或 Phase 5 完成

### 用户故事依赖

- **US1 (P1)**：仅依赖基础阶段；可独立交付审计报告
- **US2 (P2)**：依赖 US1 产出报告；可独立完成整改与复检
- **US3 (P3)**：依赖 US2 整改完成；可独立交付 MR 清单与可选 CI

### 可并行任务

- T001 与 T002 可并行（不同文件）
- T012 与 T013 可并行（不同文件）
- T015 与 T016 可并行（构建 vs 文档校验）
- 同一阶段内未标注 [P] 的任务按顺序执行

---

## 并行示例：用户故事 1

```text
# US1 为顺序执行：先 T004 填满报告矩阵，再 T005 汇总与例外说明
T004 → T005
```

---

## 并行示例：用户故事 2

```text
# 按模块可并行（若多人）：T006 (auth)、T007 (user)、T008 (role/dept/...)、T009 (userRole/rolePermission) 可并行
# T010、T011 依赖 T006–T009 完成
T006, T007, T008, T009 (并行) → T010 → T011
```

---

## 实施策略

### MVP 优先（仅用户故事 1）

1. 完成 Phase 1：准备  
2. 完成 Phase 2：基础  
3. 完成 Phase 3：用户故事 1  
4. **暂停并验收**：交付并查阅审计报告，确认覆盖全部接口与 C1–C6  
5. 可将报告用于排期与 US2 任务拆分

### 增量交付

1. 准备 + 基础 → 接口清单与报告模板就绪  
2. 完成 US1 → 审计报告可查阅（MVP）  
3. 完成 US2 → 接口整改与复检通过  
4. 完成 US3 → 持续验证机制就绪  
5. 收尾 → 构建与 quickstart 验证通过  

---

## 备注

- 所有任务均使用 `- [ ] [TaskID] [P?] [Story?] 描述（含路径）` 格式
- [P] 表示可与其他同阶段的 [P] 任务并行
- [US1]/[US2]/[US3] 仅用于用户故事阶段任务，便于追溯
- 整改时遵守宪法原则 7（中文注释）、原则 8（DRY）；破坏性变更采用版本化或兼容策略并记录
