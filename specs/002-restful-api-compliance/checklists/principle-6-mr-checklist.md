# 原则 6 MR 检查清单（RESTful API）

**用途**：在 Merge Request 中自检或评审时使用，确保新增/修改的接口符合原则 6。

**依据**：`.specify/memory/constitution.md` 原则 6、[principle-6-checklist.md](./principle-6-checklist.md)（C1–C6）。

---

## 本 MR 是否涉及 HTTP 接口变更？

- [ ] **否** → 无需勾选下方 C1–C6，可直接合入
- [ ] **是** → 对本次变更涉及的**每个**新增/修改接口完成下方检查

---

## 每个接口需满足（C1–C6）

| 检查项 | 内容 | 自检 |
|--------|------|------|
| **C1** | HTTP 方法为 GET / POST / PUT / DELETE / PATCH 之一 | □ |
| **C2** | URL 为名词（复数），路径中无 get/create/delete/list 等动词 | □ |
| **C3** | 响应为 JSON，且使用统一 Result&lt;T&gt; 包装 | □ |
| **C4** | 分页用 `page`、`size`，排序用 `sort`（或已文档化例外） | □ |
| **C5** | URL 含版本或文档中已说明版本策略 | □ |
| **C6** | API 文档（Swagger/OpenAPI/README）已标注该接口的 HTTP 方法与主要状态码 | □ |

---

## 例外

若某接口确需违反 C1–C6（如认证类 RPC 风格路径），请：

- [ ] 在审计报告或本 MR 描述中**说明理由**（如“认证接口业界惯例”）
- [ ] 在 `specs/002-restful-api-compliance/audit-report.md` 的「例外」段**登记该接口**

未经登记的例外在门禁/复检中视为不通过。

---

## 签署

- 自检人：__________
- 评审人（如要求）：__________
