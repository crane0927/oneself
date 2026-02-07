# RESTful 原则 6 审计报告

**报告标识**：2026-02-06-v1  
**审计执行时间**：2026-02-06  
**审计范围**：oneself-auth、oneself-system、oneself-system-api

---

## 输出格式说明

- 本报告按「接口」为行、「检查项 C1–C6」为列，记录符合性结果。
- 每格取值：**通过** / **不通过**（须在「不符合详情」中写原因与建议）/ **例外**（须在「例外记录」中写理由）。
- 等价 JSON 结构见 [contracts/audit-report.schema.json](contracts/audit-report.schema.json)。

---

## 结果矩阵（按接口 × 检查项）

| 模块 | 路径 | 方法 | C1 | C2 | C3 | C4 | C5 | C6 |
|------|------|------|----|----|----|----|----|-----|
| oneself-auth | /auth/login | POST | 通过 | 例外 | 通过 | — | 不通过 | 待确认 |
| oneself-auth | /auth/logout | DELETE | 通过 | 例外 | 通过 | — | 不通过 | 待确认 |
| oneself-auth | /auth/refresh | POST | 通过 | 例外 | 通过 | — | 不通过 | 待确认 |
| oneself-auth | /auth/captcha | GET | 通过 | 例外 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /user/get/user/by/{name} | GET | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /user/list/by/dept/{deptId} | GET | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /user/page | POST | 通过 | 不通过 | 通过 | 不通过 | 不通过 | 待确认 |
| oneself-system | /user（其余 CRUD） | 多种 | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /role/page | POST | 通过 | 不通过 | 通过 | 不通过 | 不通过 | 待确认 |
| oneself-system | /role/list/all | GET | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /role/tree | GET | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /role（其余 CRUD） | 多种 | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /dept/page、/list/all、/tree | 多种 | 通过 | 部分不通过 | 通过 | 不通过 | 不通过 | 待确认 |
| oneself-system | /dept（其余 CRUD） | 多种 | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /permission/page、/tree、/listByRole/{roleId} | 多种 | 通过 | 不通过 | 通过 | 不通过 | 不通过 | 待确认 |
| oneself-system | /permission（其余 CRUD） | 多种 | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /configuration/page | POST | 通过 | 不通过 | 通过 | 不通过 | 不通过 | 待确认 |
| oneself-system | /configuration（其余 CRUD） | 多种 | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /constraint/page | POST | 通过 | 不通过 | 通过 | 不通过 | 不通过 | 待确认 |
| oneself-system | /constraint（其余 CRUD） | 多种 | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /userRole/assign | POST | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /userRole/listByUser/{userId} | GET | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /userRole/deleteByUser/{userId} | DELETE | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /userRole/deleteByUserAndRoles/{userId} | DELETE | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system | /roles/{roleId}/permissions（及 batch） | GET/POST/DELETE | 通过 | 通过 | 通过 | — | 不通过 | 待确认 |
| oneself-system-api | /get/user/by/{name}（Feign） | GET | 通过 | 不通过 | 通过 | — | 不通过 | 待确认 |

说明：C4「—」表示该接口无分页；C5 当前项目未采用 URL 版本，统一记不通过并建议文档说明或加 /api/v1；C6 需对照实际 API 文档逐项确认。

---

## 不符合详情

- **oneself-auth 全部、oneself-system 全部**  
  **C5**：URL 未含版本且未在文档中说明版本策略。  
  **建议**：在 API 文档中说明「当前未使用 URL 版本，后续可引入 /api/v1」或增加路径前缀 /api/v1。

- **oneself-system：/user/get/user/by/{name}、UserClient.getUserByName**  
  **C2**：路径含动词 get。  
  **建议**：改为 GET `/users` 查询参数 `?name=xxx` 或 GET `/users/by-name/{name}`（名词化）。

- **oneself-system：/user/list/by/dept/{deptId}**  
  **C2**：路径含 list。  
  **建议**：改为 GET `/users?deptId=xxx` 或 GET `/departments/{deptId}/users`。

- **oneself-system：各模块 POST /page**  
  **C2/C4**：路径 segment「page」偏动作；分页未使用 query 参数 page、size、sort。  
  **建议**：改为 GET，路径保留资源名（如 /users、/roles），分页用 `?page=&size=&sort=`；若保留 POST 须在文档中记录为例外并说明理由。

- **oneself-system：/role/list/all、/dept/list/all**  
  **C2**：路径含 list。  
  **建议**：改为 GET `/roles`、GET `/depts` 或 GET `/roles/all`（all 为资源状态而非动词）。

- **oneself-system：/permission/listByRole/{roleId}**  
  **C2**：路径含 list。  
  **建议**：改为 GET `/roles/{roleId}/permissions`（与 RolePermissionController 一致）或 GET `/permissions?roleId=xxx`。

- **oneself-system：/userRole/assign、/listByUser/{userId}、/deleteByUser/{userId}、/deleteByUserAndRoles/{userId}**  
  **C2**：路径含动词 assign、list、delete。  
  **建议**：分配角色改为 POST `/users/{userId}/roles`；按用户查角色改为 GET `/users/{userId}/roles`；按用户删关联改为 DELETE `/users/{userId}/roles` 或 DELETE `/users/{userId}/roles?roleIds=...`。

- **全部**  
  **C6**：需在 API 文档（Swagger/OpenAPI 或 README）中标注每个接口的 HTTP 方法与主要状态码（200、201、400、401、403、404、500 等）。

---

## 例外记录

- **oneself-auth：/auth/login、/auth/logout、/auth/refresh、/auth/captcha**  
  **C2**：认证接口沿用业界惯例的 RPC 风格路径（动词），与前端/网关约定已稳定，在 API 文档中标注为「原则 6 例外」，其余接口严格遵循 RESTful。  
  **理由**：research.md 与 plan 已约定认证路径为例外，暂不改为名词资源路径。

---

## 汇总

- **接口总数**：54（唯一端点）
- **通过数**（C1–C6 全部通过）：0（当前 C5/C6 普遍不通过或待确认）
- **不通过数**：见上表及不符合详情，涉及 C2、C4、C5、C6
- **例外数**：4（auth 四个接口的 C2）
- **通过率**（不含例外）：待 C6 文档补齐后复算；当前主要缺口为 C2（URL 动词）、C4（分页参数）、C5（版本或文档说明）、C6（文档标注）
