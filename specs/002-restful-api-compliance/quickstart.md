# Quickstart: RESTful 宪法符合性审计与整改

**Feature**: 002-restful-api-compliance

## 1. 执行首轮审计

1. **确定范围**：列出所有对外 HTTP 接口所在模块（如 oneself-auth、oneself-system）及 Controller/Feign 定义位置。
2. **提取接口列表**：扫描代码中的 `@RequestMapping`、`@GetMapping`、`@PostMapping` 等，得到「模块 + 路径 + HTTP 方法」列表（可手写或脚本）。
3. **逐项核对原则 6**：对每个接口按 [data-model.md](./data-model.md) 中的检查项（C1–C6）判定通过/不通过/例外，并填写：
   - 不通过：原因 + 建议改法（如“将 /getUser 改为 GET /users/{id}"）。
   - 例外：理由（如“认证接口业界惯例”）。
4. **产出报告**：使用 [contracts/audit-report.schema.json](./contracts/audit-report.schema.json) 规定的结构生成报告（JSON 或等价的 Markdown 表），并存档到 `specs/002-restful-api-compliance/` 或项目 Wiki。

## 2. 按报告整改

1. **按优先级**：先改“不通过”项中影响面小、无破坏性变更的（如仅改路径动词 → 名词）；涉及破坏性变更的，规划版本化（如 /api/v2）或兼容期。
2. **修改代码**：调整 Controller/Feign 的 URL、HTTP 方法、分页/排序参数等；确保响应仍为 `Result<T>` 包装的 JSON。
3. **更新文档**：同步更新 API 文档（Swagger/OpenAPI 或等价），标注每个接口的 HTTP 方法与主要状态码。
4. **复检**：对已改接口再跑一遍审计，确认报告中等效项为通过或已记录例外。

## 3. 持续验证

1. **评审清单**：在 MR 模板或团队规范中增加「原则 6 检查清单」，要求新增/修改接口的 MR 自检 C1–C6。清单见 [checklists/principle-6-mr-checklist.md](checklists/principle-6-mr-checklist.md)。
2. **可选 CI**：在 CI 中增加脚本，扫描本次变更涉及的 Controller 方法，检查路径是否含常见动词或非标准分页参数；首次可仅告警，稳定后改为门禁。脚本位置与规则见 [scripts/README.md](scripts/README.md)。
3. **例外**：若确需保留非 RESTful 路径（如认证），在文档与审计报告中标注为“原则 6 例外”并说明理由。

### 例外审批流程与 MR 门禁

- **例外申请**：若新增或保留的接口确需违反 C1–C6（例如认证类 RPC 路径），须在 MR 描述中说明理由，并在 `specs/002-restful-api-compliance/audit-report.md` 的「例外记录」段登记该接口及理由；未登记的例外在复检时视为不通过。
- **MR 门禁**：涉及 HTTP 接口变更的 MR，须在合入前完成 [principle-6-mr-checklist.md](checklists/principle-6-mr-checklist.md) 中 C1–C6 自检；可选 CI 脚本（见 scripts/README.md）可配置为仅告警或阻塞合入，由团队约定。

## 4. 验证（审计 → 整改 → 复检）

按本节 1 → 2 → 3 执行后，可做一次完整验证：

1. **审计**：接口清单见 [endpoint-inventory.md](./endpoint-inventory.md)，首轮报告见 [audit-report.md](./audit-report.md)。
2. **整改**：按报告「不符合详情」修改 Controller/Feign，保留旧路径时加 `@Deprecated` 并注释新路径。
3. **复检**：复检报告见 [audit-report-recheck.md](./audit-report-recheck.md)；待 T008–T010 完成后可再次执行审计并更新复检报告，直至除已文档化例外外 100% 通过。

---

## 5. 参考

- 原则 6 条文：`.specify/memory/constitution.md` 第 115–133 行。
- 检查项与报告结构：本目录下 [data-model.md](./data-model.md)、[contracts/README.md](./contracts/README.md) 与 [contracts/audit-report.schema.json](./contracts/audit-report.schema.json)。
- 审计与例外策略：[research.md](./research.md)。
