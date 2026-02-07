# 原则 6 可选 CI 脚本

**依据**：[research.md](../research.md) 持续验证机制、[plan.md](../plan.md)。

## 用途

- 扫描 Controller 中映射的路径，检查是否包含常见**动词**（如 get、create、delete、list、page 等）或**非标准分页**（如非 GET + page/size/sort）。
- 可在 CI 中作为可选步骤运行：首次建议仅**告警**，规则稳定后可改为**门禁**（失败则 MR 不通过）。

## 脚本位置与实现方式

- **推荐位置**：`.specify/scripts/bash/` 或本目录 `specs/002-restful-api-compliance/scripts/`。
- **实现方式**（任选其一）：
  1. **Bash + grep**：在仓库根执行 `grep -rn '@GetMapping\|@PostMapping\|@RequestMapping' --include='*.java' oneself-*/`，对匹配行用正则检查路径字符串是否含 `/get/`、`/list/`、`/page`、`/create`、`/delete` 等，命中则输出告警。
  2. **Maven + 注解处理**：若有现成注解处理器或 ArchUnit 测试，可增加一条规则扫描 `@*Mapping` 的 value，逻辑同上。
  3. **OpenAPI 驱动**：若项目已导出 OpenAPI，可写脚本解析 `paths`，对每个 path 检查 segment 是否包含上述动词。

当前仓库**未内置可执行脚本**，仅在本 README 中约定位置与规则；需要时可在 `.specify/scripts/bash/` 下新增例如 `check-principle6-paths.sh`，按上述方式实现并在 CI 中调用。

## 门禁策略

- 例外路径（如 `/auth/login`）应在脚本中**白名单**或通过配置文件排除，避免误报。
- 白名单与例外记录需与 `audit-report.md` 中的「例外记录」一致。
