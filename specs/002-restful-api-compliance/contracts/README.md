# Contracts: 002-restful-api-compliance

本特性**不新增对外 HTTP API**；交付物为审计流程、审计报告与接口整改，以及持续验证机制。

- **审计报告**：产出物为结构化报告（见 [data-model.md](../data-model.md)），可选格式见 `audit-report.schema.json`，供脚本或人工填写时保持一致结构。
- **原则 6 规则集**：检查项定义见 data-model 与 research；无需单独 OpenAPI，因无新端点对外暴露。
- **整改前后**：现有接口的 URL/方法/参数可能变更，变更后的契约以实际代码与 API 文档为准；若采用版本化（如 /api/v2），v2 契约在文档或 OpenAPI 中维护。

**契约含义**：本目录约定的是「审计报告」的结构契约（便于工具与人工统一），而非新的服务接口契约。
