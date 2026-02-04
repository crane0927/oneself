# Contracts: 001-constitution-compliance

本特性为**宪法合规分析与改造**，不新增或变更对外 HTTP/Feign API 契约。

- 现有 API 的 URL、请求/响应体保持不变；仅内部类型（如 API 模块中 VO → DTO）、包路径、配置与资产（SQL、Dockerfile）发生变更。
- 若改造中涉及 Feign 接口入参/返回值类型重命名（如 UserVO → UserDTO），仍视为同一契约的实现调整，不在此目录维护 OpenAPI/独立契约文件。

**合规检查** 使用 `.specify/memory/constitution.md` 及本 spec 下的 FR/门禁作为“契约”；无需单独 contracts 文件。
