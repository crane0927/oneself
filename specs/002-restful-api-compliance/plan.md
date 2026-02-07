# Implementation Plan: 现有接口 RESTful 宪法符合性检查与整改

**Branch**: `002-restful-api-compliance` | **Date**: 2026-02-06 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `specs/002-restful-api-compliance/spec.md`

## Summary

对当前所有对外 HTTP 接口执行《宪法》原则 6（RESTful API 设计）的符合性审计，产出可读的审计报告（按接口与按规则维度）；根据报告对不符合项进行整改（URL 名词化、标准 HTTP 方法、分页/排序参数、Result 包装与状态码等）；整改完成后将原则 6 的检查纳入持续验证（评审清单或 CI 可执行规则），确保新接口在合入前通过检查。技术路径：先基于人工/脚本的审计清单产出报告，再逐项整改代码与文档，最后通过检查清单或静态规则在 MR 阶段卡点。

## Technical Context

**Language/Version**: Java 21（宪法原则 1）  
**Primary Dependencies**: Spring Boot 3.5.9、Spring Cloud 2025.0.1、Spring MVC / WebFlux（现有 Controller 与 Gateway）；审计与检查不引入新运行时依赖，可选静态分析工具由 research 决定  
**Storage**: N/A（审计报告可为文件或 Wiki；无持久化库表）  
**Testing**: Maven Surefire（现有单测）；审计与整改后全量构建与回归通过；可选契约/接口级测试验证响应形态  
**Target Platform**: JVM 21，现有微服务部署形态不变  
**Project Type**: 多模块 Maven（oneself-auth、oneself-gateway、oneself-service/oneself-system、oneself-service-api）；审计范围覆盖上述模块中对外暴露的 HTTP 接口  
**Performance Goals**: 审计报告生成在合理时间内（如全量接口 < 15 分钟）；CI 检查不显著拉长构建时间  
**Constraints**: 整改不破坏已有前端/调用方兼容性，可通过版本化（如 /api/v2）或兼容层过渡；例外项（如认证 RPC 路径）须文档化  
**Scale/Scope**: 全仓库对外 HTTP 接口（Controller + Feign 暴露路径）；重点为 oneself-auth、oneself-system 的 Controller 及 system-api 的 Feign 接口定义

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

本特性为实现原则 6 的符合性审计与整改，实施过程须遵守宪法，且交付物不得违反以下门禁：

| 原则 | 门禁条件 | 本特性适用 |
|------|----------|------------|
| 1 | Java 21 | 已满足，不引入新模块 |
| 2–3 | Spring Boot / Spring Cloud 版本 | 已满足，仅改 Controller/API 设计 |
| 4 | PostgreSQL / MyBatis-Plus 等 | 不涉及；无新存储 |
| 6 | RESTful API 设计 | **本特性交付目标**：审计 + 整改使所有接口符合原则 6；例外须记录 |
| 7 | 代码注释中文、Javadoc | 新增或修改的 Controller/文档须符合 |
| 8 | 公共方法提取、DRY | 整改时若有重复校验/转换逻辑须提取 |

**Gate 结果**: 无豁免；整改后接口须满足原则 6，新增检查逻辑（若有）须符合 7、8。认证等 RPC 风格路径若保留为例外，须在审计报告与 API 文档中明确标注并说明理由。

## Project Structure

### Documentation (this feature)

```text
specs/002-restful-api-compliance/
├── plan.md              # 本文件
├── research.md          # Phase 0：审计方式、工具选型、例外策略
├── data-model.md        # 接口定义、审计报告、原则6规则集模型
├── quickstart.md        # 如何执行审计、整改与复检
├── contracts/            # 审计报告结构/契约（无新增对外 HTTP API）
├── checklists/
│   └── requirements.md
└── tasks.md             # Phase 2 由 /speckit.tasks 生成
```

### Source Code (repository root)

现有结构不变；整改仅修改现有 Controller、Feign 接口及 API 文档。可选：在仓库根或某一模块下增加脚本或配置（如审计规则列表、CI 检查脚本），不新增微服务或新模块。

```text
oneself/
├── oneself-auth/src/.../controller/     # 审计与整改对象之一
├── oneself-service/oneself-system/
│   └── src/.../controller/             # 审计与整改对象
├── oneself-service-api/oneself-system-api/
│   └── src/.../client/                 # Feign 接口定义，纳入审计
├── oneself-gateway/                     # 路由配置与过滤器，不直接改 URL 语义
└── [可选] scripts/ 或 .specify/        # 审计脚本/规则配置，若采用文件化清单
```

**Structure Decision**: 不新增模块；审计产出为文档/报告与可选脚本；整改仅修改现有 controller、client 与 API 文档，保持与 001-constitution-compliance 的仓库布局一致。

## Complexity Tracking

本特性为合规审计与接口整改，不新增架构复杂度。若某条原则 6 子项以“例外”保留（如 `/auth/login`），在 research.md 与审计报告中记录理由与收敛计划（若有）。

| 例外（若存在） | 原因 | 收敛计划 |
|----------------|------|----------|
| 认证接口保留 RPC 风格路径（/login、/logout、/refresh、/captcha） | 业界惯例，且与前端/网关约定已稳定 | 在 API 文档与审计报告中标注为“原则 6 例外”，说明理由；暂无迁移计划 |
