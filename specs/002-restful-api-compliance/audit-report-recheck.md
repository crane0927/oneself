# RESTful 原则 6 复检报告

**复检标识**：2026-02-06-recheck  
**复检时间**：2026-02-06  
**基准报告**：audit-report.md（2026-02-06-v1）

---

## 复检范围

- 已整改接口：oneself-system 用户相关（UserController、UserClient）及 auth 调用方（UserDetailsServiceImpl 已切至新路径）。
- 未整改接口：Role、Dept、Permission、Configuration、Constraint、UserRole、RolePermission 等仍按原报告「不符合详情」待后续迭代。

---

## 已整改项复检结果

| 模块 | 原路径/问题 | 整改后 | C1 | C2 | C3 | C4 | C5 | C6 |
|------|-------------|--------|----|----|----|----|----|-----|
| oneself-system | GET /user/get/user/by/{name} | 新增 GET /user/by-name/{name}，旧路径保留并废弃 | 通过 | 通过（新路径） | 通过 | — | 待文档 | 待文档 |
| oneself-system | GET /user/list/by/dept/{deptId} | 新增 GET /user/by-dept/{deptId}，旧路径保留并废弃 | 通过 | 通过（新路径） | 通过 | — | 待文档 | 待文档 |
| oneself-system | POST /user/page | 新增 GET /user?page=&size=&sort=，旧路径保留并废弃 | 通过 | 通过（新路径） | 通过 | 通过 | 待文档 | 待文档 |
| oneself-system-api | GET /get/user/by/{name}（Feign） | 新增 getUserByUsername → /by-name/{name}，getUserByName 保留并废弃 | 通过 | 通过（新路径） | 通过 | — | 待文档 | 待文档 |
| oneself-auth | 调用 UserClient | 已改为 getUserByUsername，走新路径 | — | — | — | — | — | — |

说明：C5、C6 与全项目策略一致，需在 T010 中统一更新 API 文档与版本说明后再次复检。

---

## 例外（未改且已文档化）

- oneself-auth：/auth/login、/auth/logout、/auth/refresh、/auth/captcha — C2 例外，理由见 audit-report.md。

---

## 汇总

- **本次复检通过（原则 6 新路径）**：用户相关 4 处（Controller 3 + Feign 1），auth 调用已切新路径。
- **仍不通过（待后续整改）**：见 audit-report.md 中 Role、Dept、Permission、Configuration、Constraint、UserRole、RolePermission 等不符合详情；T008、T009 完成后需再次复检。
- **100% 通过条件**：T008、T009、T010 完成且 C5/C6 文档化后，重新执行本复检并更新上表。
