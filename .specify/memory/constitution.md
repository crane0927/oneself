<!--
Sync Impact Report:
Version: 1.0.0 → 1.1.0
最后修订: 2026-02-06
说明: 原则 6 新增「分页查询使用公共 PageReq、不必遵守 RESTful 分页形式」的例外与强制要求。
修改的原则: 原则 6（RESTful API 设计）— 新增分页接口必须使用 PageReq，且分页接口在分页参数形式上不必遵守 RESTful
新增章节: 无
移除章节: 无
模板更新状态:
  - .specify/templates/plan-template.md: ✅ 已存在 Constitution Check，无需修改
  - .specify/templates/spec-template.md: ✅ 无需修改
  - .specify/templates/tasks-template.md: ✅ 无需修改
后续待办: 无
-->

# 项目宪法

**项目名称**: oneself  
**版本**: 1.1.0  
**批准日期**: 2026-02-04  
**最后修订日期**: 2026-02-06

## 概述

本宪法定义了 oneself 项目的核心原则、技术标准和治理规则。oneself 是一个基于 Spring Boot 和 Spring Cloud 的企业级快速开发框架，旨在提供高效、规范、可维护的微服务开发解决方案。

## 技术栈原则

### 原则 1: Java 版本要求

**规则**: 项目必须使用 JDK 21 作为最低和唯一支持的 Java 版本。

**理由**: JDK 21 是长期支持（LTS）版本，提供了现代 Java 特性（如虚拟线程、模式匹配、记录类等），能够提升开发效率和运行时性能。所有代码必须兼容 JDK 21，不得使用已废弃的 API 或特性。

**验证**: 
- `pom.xml` 或 `build.gradle` 中必须明确指定 Java 版本为 21
- CI/CD 流水线必须使用 JDK 21 进行构建和测试
- 代码审查时检查是否使用了 JDK 21 不支持的特性

### 原则 2: Spring Boot 版本锁定

**规则**: 项目必须使用 Spring Boot 3.5.9 版本，不得随意升级或降级。

**理由**: Spring Boot 3.5.9 与 JDK 21 完全兼容，提供了稳定的框架特性和安全补丁。版本锁定确保团队使用统一的技术栈，避免因版本差异导致的兼容性问题。

**验证**:
- `pom.xml` 中的 `spring-boot-starter-parent` 版本必须为 3.5.9
- 所有 Spring Boot 相关依赖的版本必须由父 POM 管理，不得显式指定版本号

### 原则 3: Spring Cloud 生态版本

**规则**: 项目必须使用 Spring Cloud 2025.0.1 和 Spring Cloud Alibaba 2025.0.0.0 版本。

**理由**: 这两个版本与 Spring Boot 3.5.9 完全兼容，提供了完整的微服务解决方案，包括服务注册与发现、配置管理、网关、熔断降级等功能。版本统一确保微服务组件之间的兼容性。

**验证**:
- `pom.xml` 的 `dependencyManagement` 中必须包含这两个 BOM
- 所有 Spring Cloud 和 Spring Cloud Alibaba 组件的版本必须由 BOM 管理

### 原则 4: 数据库技术选型

**规则**: 项目必须使用 PostgreSQL 作为主要关系型数据库。

**理由**: PostgreSQL 是功能强大的开源关系型数据库，具有优秀的性能、可靠性和扩展性。支持 ACID 事务、复杂查询、JSON 数据类型、全文搜索等高级特性，适合企业级应用场景。

**具体要求**:
- 所有持久化数据必须存储在 PostgreSQL 数据库中
- 数据库连接配置必须使用 MyBatis-Plus
- 数据库迁移使用 Flyway 或 Liquibase 进行版本管理
- 禁止使用其他关系型数据库（如 MySQL、Oracle）作为主数据库
- 如需使用其他数据库（如 Redis、MongoDB），仅作为缓存或特定场景的辅助存储
- 不需要数据库连接的模块（如网关、配置中心等）无需配置数据源

**验证**:
- 需要数据库连接的模块必须使用 MyBatis-Plus 进行数据访问
- 数据库迁移脚本必须使用 Flyway 或 Liquibase 管理
- 代码审查时检查是否有直接使用其他关系型数据库的情况
- 代码审查时检查是否使用了 Spring Data JPA 或其他 ORM 框架

### 原则 5: 组件优先使用 Spring Cloud 生态

**规则**: 所有功能组件必须优先使用 Spring Cloud 和 Spring Cloud Alibaba 提供的官方组件，避免引入第三方替代方案。

**具体要求**:
- **服务注册与发现**: 优先使用 Nacos（Spring Cloud Alibaba），避免使用 Eureka、Consul 等
- **配置管理**: 优先使用 Nacos Config，避免使用 Spring Cloud Config Server
- **API 网关**: 优先使用 Spring Cloud Gateway，避免使用 Zuul、Kong 等
- **服务调用**: 优先使用 OpenFeign，避免使用 RestTemplate、OkHttp 等原生 HTTP 客户端
- **熔断降级**: 优先使用 Sentinel（Spring Cloud Alibaba），避免使用 Hystrix
- **分布式事务**: 优先使用 Seata（Spring Cloud Alibaba），避免使用其他分布式事务方案
- **消息队列**: 优先使用 RocketMQ（Spring Cloud Alibaba），避免使用 RabbitMQ、Kafka 等
- **链路追踪**: 优先使用 SkyWalking（企业常用方案，推荐）或 Spring 官方 Micrometer Tracing 生态；避免 Zipkin/Jaeger 等非统一方案
- **限流**: 优先使用 Sentinel，避免使用 Guava RateLimiter 等
- **负载均衡**: 使用 Spring Cloud LoadBalancer（Spring Cloud 官方组件）

**理由**: 
- Spring Cloud 和 Spring Cloud Alibaba 组件与 Spring Boot 深度集成，配置简单，维护成本低
- 官方组件经过充分测试，稳定性和兼容性有保障
- 统一的组件生态降低学习成本，便于团队协作
- 避免组件冲突和版本兼容性问题

**例外情况**:
- 如果 Spring Cloud 生态中没有对应组件，可以引入第三方组件，但必须在代码注释和文档中说明原因
- 特殊业务场景需要特定组件时，需要团队评审批准

**验证**:
- 代码审查时检查依赖引入，确认优先使用 Spring Cloud 生态组件
- `pom.xml` 中不应包含已被 Spring Cloud 生态替代的第三方组件（如 Hystrix、Eureka）
- 引入非 Spring Cloud 生态组件时，必须提供充分的理由说明

## 架构与设计原则

### 原则 6: RESTful API 设计

**规则**: 所有 HTTP 接口必须严格遵循 RESTful 设计风格；分页查询接口必须使用项目公共的 `PageReq`，且在分页参数形式上不必遵守 RESTful 约定。

**具体要求**:
- 使用标准 HTTP 方法（GET、POST、PUT、DELETE、PATCH）
- URL 路径使用名词复数形式，避免动词（如 `/api/users` 而非 `/api/getUsers`）
- 使用 HTTP 状态码表示操作结果（200、201、204、400、401、403、404、500 等）
- 响应体使用 JSON 格式，统一使用 `Result<T>` 包装
- **分页查询接口**：必须使用项目公共的 `PageReq`（或等价封装）承载分页与排序条件；此类接口在「分页参数形式」上不必遵守 RESTful 的 GET + query 参数约定，允许使用 POST + 请求体（PageReq）等形式实现分页查询
- 非分页的列表/查询接口仍推荐使用 `page`、`size`、`sort` 等 query 参数（GET）
- 版本控制通过 URL 路径实现（如 `/api/v1/users`）

**理由**: RESTful 风格是业界标准，提供了一致的 API 设计规范；分页场景统一使用 PageReq 可保证参数形态一致、便于复用与校验，且与项目现有公共组件一致，故在分页参数形式上豁免严格 RESTful 要求。

**验证**:
- 代码审查时检查 Controller 层的 URL 设计和 HTTP 方法使用
- 分页查询接口必须使用公共 PageReq（或等价封装），禁止手写分散的 page/size 参数对象
- API 文档必须明确标注每个接口的 HTTP 方法和状态码
- 禁止在 URL 中使用动词（如 `/createUser`、`/deleteUser`）

### 原则 7: 代码注释规范

**规则**: 代码注释优先使用中文，类、方法、复杂逻辑必须添加中文注释。

**具体要求**:
- 所有公共类必须使用 Javadoc 格式的中文注释，包含类描述、作者、创建时间
- 所有公共方法必须使用 Javadoc 格式的中文注释，包含方法描述、参数说明、返回值说明、异常说明
- 复杂业务逻辑必须添加行内中文注释，解释实现思路和关键步骤
- 配置类、工具类、常量类必须详细注释每个字段和方法的用途
- 接口（Interface）必须注释其设计意图和使用场景

**理由**: 中文注释便于国内开发团队理解和维护代码，降低沟通成本，提高代码可读性。中文注释有助于新成员快速理解业务逻辑。

**验证**:
- 代码审查时检查关键类和方法是否有中文注释
- 使用代码质量工具（如 SonarQube）检查注释覆盖率
- 禁止使用无意义的英文注释（如 `// TODO`、`// FIXME` 应改为中文说明）

### 原则 8: 代码复用与公共方法提取

**规则**: 重复代码必须提取为公共方法或工具类，遵循 DRY（Don't Repeat Yourself）原则。

**具体要求**:
- 相同或相似的业务逻辑必须提取到 Service 层的公共方法
- 通用的工具方法必须放在 `utils` 包下的工具类中，方法声明为 `public static`
- 重复的验证逻辑必须提取为公共验证方法或使用注解
- 重复的异常处理逻辑必须使用统一异常处理器（`@ControllerAdvice`）
- 重复的数据转换逻辑必须使用 MapStruct 或自定义转换器
- 公共方法必须添加完整的中文注释，说明使用场景和注意事项

**理由**: 代码复用减少维护成本，提高代码质量，确保业务逻辑的一致性。公共方法便于单元测试和功能扩展。

**验证**:
- 代码审查时识别重复代码模式，要求重构
- 使用代码分析工具（如 PMD、Checkstyle）检测重复代码
- 新增功能时优先检查是否有可复用的公共方法

### 原则 9: 设计模式应用

**规则**: 在适当场景下使用经典设计模式，提高代码的可维护性、可扩展性和可测试性。

**推荐使用的设计模式**:

1. **策略模式（Strategy Pattern）**
   - 适用于：多种算法或业务规则需要动态切换的场景
   - 示例：支付方式选择、数据验证规则、排序策略等

2. **工厂模式（Factory Pattern）**
   - 适用于：对象创建逻辑复杂，需要统一管理的场景
   - 示例：数据源工厂、消息处理器工厂、转换器工厂等

3. **建造者模式（Builder Pattern）**
   - 适用于：创建复杂对象，需要灵活配置的场景
   - 示例：复杂查询条件构建、配置对象构建等

4. **模板方法模式（Template Method Pattern）**
   - 适用于：多个类有相似的算法骨架，但具体步骤不同的场景
   - 示例：数据处理流程、审批流程、报表生成等

5. **观察者模式（Observer Pattern）**
   - 适用于：对象间一对多依赖关系，一个对象状态改变需要通知多个对象的场景
   - 示例：事件发布订阅、消息通知、状态变更监听等

6. **适配器模式（Adapter Pattern）**
   - 适用于：需要适配不同接口或第三方组件的场景
   - 示例：第三方 API 适配、数据格式转换等

7. **单例模式（Singleton Pattern）**
   - 适用于：确保类只有一个实例的场景（谨慎使用，优先使用 Spring 的 Bean 管理）
   - 注意：在 Spring 环境中，通常使用 `@Component` 或 `@Service` 注解即可

8. **责任链模式（Chain of Responsibility Pattern）**
   - 适用于：多个对象可以处理同一请求，但处理优先级不同的场景
   - 示例：权限验证链、数据校验链、审批流程等

**使用原则**:
- **适度使用**: 不要为了使用设计模式而使用，应根据实际业务需求选择
- **保持简单**: 优先使用简单直接的实现方式，只有在复杂度确实需要时才引入设计模式
- **文档说明**: 使用设计模式时，必须在代码注释中说明使用的模式和设计意图
  - **注释要求**: 必须在类或方法上添加中文注释，明确说明：
    1. **使用的设计模式名称**: 明确标注使用的设计模式（如策略模式、工厂模式等）
    2. **使用原因**: 说明为什么在此场景下使用该设计模式，解决了什么问题
  - **注释位置**: 
    - 使用设计模式的类必须在类级别添加 Javadoc 注释
    - 使用设计模式的关键方法必须在方法级别添加注释
  - **注释示例**:
    ```java
    /**
     * 支付策略工厂类
     * 
     * 设计模式: 工厂模式（Factory Pattern）
     * 使用原因: 支付方式有多种实现（支付宝、微信、银行卡等），需要根据支付类型动态创建对应的支付策略对象。
     * 通过工厂模式统一管理支付策略的创建逻辑，避免在业务代码中直接 new 对象，提高代码的可维护性和可扩展性。
     */
    public class PaymentStrategyFactory {
        // ...
    }
    ```
- **团队共识**: 复杂的设计模式使用前应经过团队评审，确保团队成员理解

**避免过度设计**:
- 不要在不必要的地方使用复杂的设计模式
- 不要为了展示技术能力而引入不必要的抽象层
- 优先使用 Spring 框架提供的特性（如依赖注入、AOP）来实现解耦

**理由**: 
- 设计模式是经过验证的解决方案，能够解决常见的软件设计问题
- 适当使用设计模式可以提高代码的可维护性和可扩展性
- 统一的模式使用有助于团队理解和维护代码
- 避免过度设计，保持代码简洁易懂

**验证**:
- 代码审查时检查复杂业务逻辑是否可以使用设计模式优化
- 检查设计模式的使用是否合理，避免过度设计
- **注释检查**: 确保使用设计模式的类和方法都有完整的中文注释，包含：
  - 设计模式名称（必须）
  - 使用原因说明（必须）
  - 设计意图和解决的问题（推荐）
- 使用代码质量工具检查注释覆盖率，确保设计模式相关代码的注释完整

### 原则 10: 配置文件格式规范

**规则**: 所有 resource 目录下的配置文件必须使用 YAML 格式（`.yaml` 或 `.yml` 扩展名），禁止使用 Properties 格式（`.properties`）。

**具体要求**:
- 所有 Spring Boot 配置文件必须使用 `application.yaml` 或 `application.yml`，禁止使用 `application.properties`
- 所有环境配置文件（如 `application-dev.yaml`、`application-prod.yaml`）必须使用 YAML 格式
- 所有自定义配置文件（如日志配置、数据源配置等）必须使用 YAML 格式
- YAML 文件必须遵循标准的 YAML 语法规范，使用 2 个空格缩进
- 配置项 Key 使用小写字母和连字符（kebab-case），如 `spring.application.name`
- 敏感配置（如密码、密钥）应使用环境变量或配置中心（Nacos）管理，避免硬编码

**理由**: 
- YAML 格式比 Properties 格式更易读，支持层级结构和数据类型
- YAML 格式便于管理复杂的配置结构，减少配置文件的复杂度
- 统一的配置格式提高团队协作效率，降低配置错误率
- YAML 格式与 Spring Boot 和 Spring Cloud 配置管理最佳实践一致

**例外情况**:
- 如果第三方组件强制要求使用 Properties 格式，可以在代码注释中说明原因
- 历史遗留的 Properties 配置文件应逐步迁移到 YAML 格式

**验证**:
- 代码审查时检查是否有 Properties 格式的配置文件
- CI/CD 流水线中可以添加检查，禁止提交 Properties 格式的配置文件
- 新创建的配置文件必须使用 YAML 格式

## 项目结构原则

### 原则 11: 模块化设计

**规则**: 项目必须采用模块化设计，遵循分层架构原则。

**标准结构**:
```
oneself/
├── oneself-gateway/        # API 网关
├── oneself-auth/           # 认证授权服务
├── oneself-common/         # 公共模块
│   ├── oneself-common-infra/     # 基础设施模块
│   │   ├── oneself-common-infra-web/      # Web 相关工具
│   │   ├── oneself-common-infra-redis/    # Redis 相关工具
│   │   ├── oneself-common-infra-db/       # 数据库相关工具
│   │   └── oneself-common-infra-logging/  # 日志相关
│   └── oneself-common-feature/   # 功能特性模块
│       ├── oneself-common-feature-core/      # 核心工具类
│       └── oneself-common-feature-security/   # 安全相关
├── oneself-service/        # 服务模块
│   ├── oneself-system/     # 系统管理服务
│   └── ... 
└── oneself-service-api/        # API 接口定义
    ├── oneself-system-api/     # 系统管理服务 API
    └── ...
```

**理由**: 模块化设计便于代码组织、团队协作和功能扩展，符合微服务架构的最佳实践。

**模块文档要求**:
- 每个模块必须在根目录下提供 `README.md` 文件
- `README.md` 必须包含以下内容：
  - 模块简介：说明模块的用途和职责
  - 主要功能：列出模块提供的核心功能
  - 快速开始：提供依赖配置和使用示例
  - 相关文档：链接到详细文档（如 quickstart.md、spec.md 等）
- `README.md` 使用中文编写，遵循项目文档规范

**理由**: 
- 模块文档帮助开发人员快速了解模块功能和使用方法
- 统一的文档格式提高项目可维护性
- 便于新成员快速上手和理解项目结构

**验证**:
- 代码审查时检查新模块是否包含 README.md
- CI/CD 流水线可以添加检查，确保所有模块都有 README.md
- 定期审查模块文档的完整性和准确性

### 原则 12: 包结构组织规范

**规则**: 包结构组织方式必须根据模块类型采用不同的组织策略。

**具体要求**:

1. **业务相关模块**（如 `oneself-system`、`oneself-auth`、`oneself-order` 等业务服务模块）:
   - **组织方式**: 按业务模块再按技术分层
   - **包结构**: `com.oneself.{module-name}.{business-module}.{layer}`
   - **示例**:
     ```java
     package com.oneself.system.user.controller;
     package com.oneself.system.user.service;
     package com.oneself.system.user.mapper;
     package com.oneself.system.order.controller;
     package com.oneself.system.order.service;
     ```
   - **理由**: 业务模块优先组织，同一业务模块的所有代码（controller、service、mapper）聚合在一起，便于业务功能开发和维护，符合领域驱动设计（DDD）理念

2. **技术相关模块**（如 `oneself-gateway`、`oneself-common-infra-*`、`oneself-common-feature-*` 等基础设施和功能特性模块）:
   - **组织方式**: 按技术分层再按业务模块
   - **包结构**: 
     - 独立技术模块: `com.oneself.{module-name}.{layer}`
     - 公共技术模块: `com.oneself.common.{category}.{module-name}.{layer}`
   - **示例**:
     ```java
     // 独立技术模块（如 gateway）
     package com.oneself.gateway.config;
     package com.oneself.gateway.filter;
     package com.oneself.gateway.exception;
     
     // 公共技术模块（如 common-infra-web）
     package com.oneself.common.infra.web.config;
     package com.oneself.common.infra.web.filter;
     package com.oneself.common.infra.web.exception;
     package com.oneself.common.infra.redis.config;
     package com.oneself.common.infra.redis.util;
     ```
   - **理由**: 技术模块以技术层为主，相同技术层的代码集中在一起，便于统一管理和规范，便于技术层优化和学习

**模块类型判断**:

- **业务相关模块**: 包含业务逻辑、处理业务数据、提供业务服务的模块
  - 示例: `oneself-system`、`oneself-auth`、`oneself-order` 等
  - **判断标准**: 模块包含具体的业务领域逻辑，处理业务实体和业务规则
- **技术相关模块**: 提供基础设施能力、通用工具类、技术组件的模块
  - 示例: `oneself-gateway`（API 网关）、`oneself-common-infra-web`、`oneself-common-infra-redis`、`oneself-common-infra-db`、`oneself-common-feature-core` 等
  - **判断标准**: 模块提供技术基础设施能力，不包含具体业务逻辑，主要处理技术层面的功能（如路由转发、配置管理、工具类等）

**验证**:
- 代码审查时检查包结构是否符合模块类型的组织方式
- 业务模块的包结构必须按业务模块再按技术分层组织
- 技术模块的包结构必须按技术分层再按业务模块组织
- 新创建的类必须放在符合规范的包路径下

**理由**: 
- 不同的组织方式适用于不同类型的模块，提高代码可维护性和开发效率
- 业务模块按业务组织，便于业务功能开发和模块拆分
- 技术模块按技术组织，便于技术层统一管理和优化
- 统一的包结构规范提高代码可读性和团队协作效率

### 原则 13: DTO 与 VO 使用规范

**规则**: 项目必须明确区分 DTO（Data Transfer Object）和 VO（View Object）的使用场景，禁止混用。

**定义**:

1. **DTO（Data Transfer Object，数据传输对象）**
   - **用途**: 用于服务间数据传输、跨层数据传输、API 接口契约定义
   - **使用场景**:
     - **服务间调用**: Feign 接口的请求参数和响应结果（定义在 `oneself-service-api` 模块中）
     - **跨层传输**: Service 层与 Controller 层之间的数据传输
     - **API 契约**: 作为 API 接口的输入输出对象，定义在 `oneself-service-api` 模块中
   - **特点**:
     - 关注数据传输，不包含业务逻辑
     - 字段通常与数据库实体或业务对象对应
     - 可以包含多个实体的组合字段
     - 必须遵循接口兼容性规则（新增字段必须可空或提供默认值）
   - **包路径**: 
     - API 模块: `com.oneself.{service-name}.api.v{version}.model.dto`
     - 业务模块: `com.oneself.{module-name}.{business-module}.model.dto`

2. **VO（View Object，视图对象）**
   - **用途**: 用于前端展示、页面渲染、用户交互
   - **使用场景**:
     - **Controller 返回**: Controller 层返回给前端的数据对象
     - **页面展示**: 包含前端需要的展示字段和格式化数据
     - **用户交互**: 包含前端表单提交、查询条件等
   - **特点**:
     - 关注前端展示需求，字段可能包含格式化后的数据（如日期字符串、金额格式化等）
     - 可以包含多个 DTO 或实体的组合字段
     - 可以包含前端特有的字段（如状态文本、操作按钮等）
     - 不参与服务间调用，仅用于前后端交互
   - **包路径**: `com.oneself.{module-name}.{business-module}.model.vo`

**使用范围规定**:

1. **oneself-service-api 模块**:
   - **必须使用 DTO**: 所有 Feign 接口的请求参数和响应结果必须使用 DTO
   - **禁止使用 VO**: API 模块中禁止定义 VO，因为 API 模块是服务间调用的契约，不涉及前端展示
   - **示例**: `UserDTO`、`UserAuthoritiesDTO` 等定义在 `oneself-system-api` 中

2. **oneself-service 模块（业务服务）**:
   - **Service 层**: 
     - 内部方法参数和返回值可以使用 Entity 或 DTO
     - 与 Controller 层交互时使用 DTO
     - 调用其他服务时使用 API 模块定义的 DTO
   - **Controller 层**:
     - 接收前端请求参数可以使用 VO（如 `UserQueryVO`、`UserCreateVO`）
     - 返回给前端的数据使用 VO（如 `UserVO`、`UserListVO`）
     - 与 Service 层交互时使用 DTO
   - **Mapper 层**: 使用 Entity，不涉及 DTO 或 VO

3. **数据流转示例**:
   ```
   前端 → Controller (VO) → Service (DTO) → Mapper (Entity) → 数据库
   数据库 → Mapper (Entity) → Service (DTO) → Controller (VO) → 前端
   服务A → Feign (DTO) → 服务B Controller (DTO) → Service (Entity/DTO) → 数据库
   ```

**命名规范**:

- **DTO 命名**: `{EntityName}DTO`，如 `UserDTO`、`OrderDTO`、`UserAuthoritiesDTO`
- **VO 命名**: `{EntityName}VO` 或 `{Purpose}VO`，如 `UserVO`、`UserListVO`、`UserQueryVO`、`UserCreateVO`
- **包路径规范**:
  - DTO: 
    - API 模块: `com.oneself.{service-name}.api.v{version}.model.dto`
    - 业务模块: `com.oneself.{module-name}.{business-module}.model.dto`
  - VO: `com.oneself.{module-name}.{business-module}.model.vo`

**转换规则**:

1. **Entity → DTO**: Service 层将 Entity 转换为 DTO 返回给 Controller 层或通过 Feign 返回给其他服务
2. **DTO → VO**: Controller 层将 DTO 转换为 VO 返回给前端（可以包含格式化、字段组合等操作）
3. **VO → DTO**: Controller 层将前端提交的 VO 转换为 DTO 传递给 Service 层
4. **转换工具**: 可以使用 MapStruct、BeanUtils 等工具进行对象转换，但必须保证转换的正确性

**禁止事项**:

1. **禁止在 API 模块中使用 VO**: `oneself-service-api` 模块中禁止定义 VO，只能定义 DTO
2. **禁止在服务间调用中使用 VO**: Feign 接口的请求参数和响应结果必须使用 DTO，禁止使用 VO
3. **禁止混用**: 同一场景下不能同时使用 DTO 和 VO，必须明确选择一种
4. **禁止直接返回 Entity**: Controller 层禁止直接返回 Entity 对象，必须转换为 VO 或 DTO

**理由**: 
- 明确区分 DTO 和 VO 的使用场景，避免概念混淆和误用
- DTO 用于服务间调用和跨层传输，保证接口契约的稳定性
- VO 用于前端展示，可以灵活调整字段以满足前端需求
- 分离关注点，提高代码可维护性和可扩展性
- 符合分层架构原则，各层职责清晰

**验证**:
- 代码审查时检查 API 模块中是否定义了 VO（应使用 DTO）
- 检查 Controller 层是否直接返回 Entity（应转换为 VO）
- 检查 Feign 接口是否使用了 VO（应使用 DTO）
- 检查包路径是否符合规范（所有对象类型必须在 `model` 包下，DTO 在 `model.dto`，VO 在 `model.vo`，Entity 在 `model.entity`，BO 在 `model.bo`，枚举在 `model.enums`）
- 检查命名是否符合规范（DTO 以 `DTO` 结尾，VO 以 `VO` 结尾）

**其他对象类型说明**:

3. **Entity（实体类）**
   - **用途**: 对应数据库表结构，用于数据持久化
   - **使用场景**:
     - **Mapper 层**: Mapper 接口的输入输出参数
     - **Service 层**: 内部业务逻辑处理，与数据库交互
     - **数据持久化**: 通过 MyBatis-Plus 进行数据库操作
   - **特点**:
     - 与数据库表结构一一对应
     - 包含数据库字段映射注解（如 `@TableName`、`@TableId`、`@TableField`）
     - 可以继承 `BaseEntity` 使用审计字段（创建时间、更新时间、创建人、更新人等）
     - 支持逻辑删除（使用 `@TableLogic` 注解）
     - 不直接暴露给 Controller 层或外部服务
   - **包路径**: `com.oneself.{module-name}.{business-module}.model.entity`
   - **命名规范**: `{EntityName}`，如 `User`、`Order`、`Product`
   - **禁止事项**:
     - 禁止在 Controller 层直接返回 Entity
     - 禁止在 Feign 接口中使用 Entity
     - 禁止在 API 模块中定义 Entity

**重要原则：所有对象类型必须放在 model 层**

**规则**: 所有对象相关的类型（DTO、VO、Entity、BO、枚举、常量等）都必须放在 `model` 包下，禁止在 `model` 包外定义对象类型。

**具体要求**:

1. **业务模块**:
   - **包路径**: `com.oneself.{module-name}.{business-module}.model.{type}`
   - **对象类型分类**:
     - `model.dto` - DTO 对象
     - `model.vo` - VO 对象
     - `model.entity` - Entity 实体类
     - `model.bo` - BO 业务对象
     - `model.enums` - 枚举常量
     - `model.constant` - 常量类（可选，简单常量可直接放在业务模块下）
   - **示例**:
     ```java
     package com.oneself.system.user.model.dto;      // DTO
     package com.oneself.system.user.model.vo;       // VO
     package com.oneself.system.user.model.entity;  // Entity
     package com.oneself.system.user.model.bo;       // BO
     package com.oneself.system.user.model.enums;   // 枚举
     ```

2. **API 模块**（`oneself-service-api`）:
   - **包路径**: `com.oneself.{service-name}.api.v{version}.model.{type}`
   - **对象类型分类**:
     - `model.dto` - DTO 对象（必须）
     - `model.enums` - 枚举常量（必须）
     - `model.constant` - 常量类（可选）
   - **禁止**: API 模块中禁止定义 VO、Entity、BO
   - **示例**:
     ```java
     package com.oneself.system.api.v1.model.dto;    // DTO
     package com.oneself.system.api.v1.model.enums;  // 枚举
     ```

3. **技术模块**（如 `oneself-gateway`、`oneself-common-*`）:
   - 根据模块特性，对象类型也应放在 `model` 包下（如果存在）
   - 工具类、配置类等非对象类型按技术分层组织

**禁止事项**:
- 禁止在 `model` 包外定义对象类型（DTO、VO、Entity、BO、枚举等）
- 禁止将枚举、常量等对象类型直接放在业务模块根包下
- 禁止在 API 模块中将枚举放在 `enums` 包下（应放在 `model.enums` 下）

**理由**: 
- 统一的对象组织方式提高代码可维护性和可读性
- `model` 包明确标识所有数据模型和对象类型
- 便于代码查找和重构
- 符合领域驱动设计（DDD）的理念
- 避免对象类型散落在不同包下，造成混乱

4. **Query/Request（查询/请求对象）**
   - **用途**: 封装查询条件、分页参数、请求参数
   - **使用场景**:
     - **Controller 层**: 接收前端查询请求参数（如 `UserQueryVO`、`OrderQueryVO`）
     - **Service 层**: 封装复杂查询条件（如 `UserQueryDTO`、`OrderQueryDTO`）
     - **分页查询**: 封装分页参数（页码、每页大小、排序字段等）
   - **特点**:
     - 关注查询条件，不包含业务逻辑
     - 可以包含多个查询字段的组合
     - 支持分页参数、排序参数
     - 字段通常可空，支持可选查询条件
   - **包路径**: 
     - Controller 层: `com.oneself.{module-name}.{business-module}.model.vo`（如 `UserQueryVO`）
     - Service 层: `com.oneself.{module-name}.{business-module}.model.dto`（如 `UserQueryDTO`）
   - **命名规范**: `{EntityName}QueryVO`、`{EntityName}QueryDTO`、`{EntityName}RequestVO`、`{EntityName}RequestDTO`
   - **示例**: `UserQueryVO`、`OrderQueryDTO`、`UserCreateRequestVO`、`OrderUpdateRequestDTO`

5. **BO（Business Object，业务对象）**
   - **用途**: 封装业务逻辑和业务规则，用于复杂业务场景
   - **使用场景**:
     - **复杂业务逻辑**: 需要封装多个实体或 DTO 的业务对象
     - **业务规则封装**: 包含业务规则和业务方法的对象
     - **领域模型**: 在领域驱动设计（DDD）中使用
   - **特点**:
     - 可以包含业务逻辑方法
     - 可以组合多个 Entity 或 DTO
     - 关注业务语义，不关注数据持久化
     - 通常在 Service 层内部使用
   - **包路径**: `com.oneself.{module-name}.{business-module}.model.bo`
   - **命名规范**: `{EntityName}BO` 或 `{BusinessPurpose}BO`，如 `UserBO`、`OrderBO`、`PaymentBO`
   - **使用建议**: 
     - 仅在复杂业务场景下使用，简单场景优先使用 Entity 或 DTO
     - 避免过度设计，保持代码简洁

6. **枚举（Enums）**
   - **用途**: 定义常量枚举值，用于状态、类型等固定值
   - **使用场景**:
     - **状态枚举**: 如用户状态、订单状态等
     - **类型枚举**: 如用户类型、支付类型等
     - **代码枚举**: 如错误码、状态码等
   - **特点**:
     - 定义固定的枚举值集合
     - 支持 JSON 序列化/反序列化
     - 提供类型安全
   - **包路径**: 
     - API 模块: `com.oneself.{service-name}.api.v{version}.model.enums`
     - 业务模块: `com.oneself.{module-name}.{business-module}.model.enums`
   - **命名规范**: `{EntityName}Status`、`{EntityName}Type`、`{Purpose}Enum`，如 `UserStatus`、`OrderType`、`PaymentStatus`
   - **禁止事项**:
     - 禁止在 `model` 包外定义枚举
     - 禁止将枚举直接放在业务模块根包下

7. **Result<T>（统一响应包装类）**
   - **用途**: 统一封装所有 HTTP 接口的响应数据
   - **使用场景**:
     - **Controller 返回**: 所有 Controller 方法的返回值
     - **Feign 接口返回**: 所有 Feign 接口的返回值
     - **统一响应格式**: 提供统一的成功/失败响应格式
   - **特点**:
     - 包含状态码（code）、消息（message）、数据（data）、时间戳（timestamp）、追踪ID（traceId）
     - 支持泛型，可以包装任意类型的数据
     - 自动从 MDC 中获取 traceId，支持分布式追踪
     - 成功响应：`Result.success(data)`
     - 失败响应：`Result.error(code, message)`
   - **包路径**: `com.oneself.common.feature.core.result`
   - **使用规范**:
     - 所有 HTTP 接口必须使用 `Result<T>` 包装响应数据
     - 禁止直接返回 DTO、VO 或 Entity
     - 禁止在 Service 层返回 `Result<T>`（Service 层返回业务对象，Controller 层包装为 Result）

8. **PageResult<T>（分页响应对象）**
   - **用途**: 统一封装分页查询结果
   - **使用场景**:
     - **分页查询返回**: 分页查询接口的返回值
     - **列表查询**: 需要返回列表数据和分页信息的场景
   - **特点**:
     - 包含列表数据（list）、总数（total）、页码（page）、每页大小（size）、总页数（pages）、追踪ID（traceId）
     - 支持泛型，可以包装任意类型的数据列表
     - 自动从 MDC 中获取 traceId
   - **包路径**: `com.oneself.common.feature.core.page`
   - **使用规范**:
     - 分页查询接口必须使用 `PageResult<T>` 包装响应数据
     - 禁止使用自定义分页对象
     - 禁止在 Service 层返回 `PageResult<T>`（Service 层返回分页数据，Controller 层包装为 PageResult）

**完整数据流转示例**:
```
前端请求:
  前端 → Controller (QueryVO) → Service (QueryDTO) → Mapper (Entity) → 数据库

数据查询:
  数据库 → Mapper (Entity) → Service (Entity/BO) → Service (DTO) → Controller (VO) → Result<VO> → 前端

服务间调用:
  服务A → Feign (QueryDTO) → 服务B Controller (QueryDTO) → Service (Entity) → Mapper (Entity) → 数据库
  数据库 → Mapper (Entity) → Service (DTO) → Controller (DTO) → Result<DTO> → Feign (DTO) → 服务A
```

**对象类型选择指南**:

| 场景 | 使用对象类型 | 包路径示例 |
|------|------------|-----------|
| 数据库表映射 | Entity | `com.oneself.system.user.model.entity.User` |
| 服务间调用（Feign） | DTO | `com.oneself.system.api.v1.model.dto.UserDTO` |
| Controller 与 Service 交互 | DTO | `com.oneself.system.user.model.dto.UserDTO` |
| Controller 返回前端 | VO | `com.oneself.system.user.model.vo.UserVO` |
| 前端查询条件 | QueryVO | `com.oneself.system.user.model.vo.UserQueryVO` |
| Service 查询条件 | QueryDTO | `com.oneself.system.user.model.dto.UserQueryDTO` |
| 复杂业务逻辑 | BO（可选） | `com.oneself.system.user.model.bo.UserBO` |
| 状态/类型枚举 | Enums | `com.oneself.system.user.model.enums.UserStatus` |
| HTTP 接口响应 | Result<T> | `Result<UserVO>`、`Result<PageResult<UserVO>>` |
| 分页查询响应 | PageResult<T> | `PageResult<UserVO>` |

**禁止事项补充**:
1. **禁止在 API 模块中定义 Entity**: `oneself-service-api` 模块中禁止定义 Entity，只能定义 DTO
2. **禁止在 Controller 层直接使用 Entity**: Controller 层禁止直接接收或返回 Entity，必须转换为 VO 或 DTO
3. **禁止在 Service 层返回 Result**: Service 层返回业务对象，Controller 层负责包装为 Result
4. **禁止混用对象类型**: 同一场景下必须使用统一的对象类型，禁止混用
5. **禁止在 model 包外定义对象类型**: 所有对象类型（DTO、VO、Entity、BO、枚举等）必须放在 `model` 包下
6. **禁止将枚举放在 model 包外**: 枚举必须放在 `model.enums` 包下，禁止直接放在业务模块根包下

### 原则 14: 模块职责边界

**规则**: 每个模块必须有明确的职责边界，禁止跨边界直接访问，必须通过定义好的接口进行交互。

**模块职责定义**:

1. **oneself-gateway（API 网关）**
   - **职责**: 统一入口、路由转发、负载均衡、限流熔断、跨域处理
   - **允许**: 
     - 路由转发到业务服务（网关的基本功能）
     - 调用认证服务进行鉴权
     - 调用配置中心获取配置
     - 记录访问日志
   - **禁止**: 
     - 包含业务逻辑
     - 直接访问数据库
     - 作为客户端主动聚合调用多个业务服务来拼装业务响应（避免 BFF/编排逻辑进入网关）
     - 业务编排和数据聚合（应通过独立的 BFF 服务或业务服务实现）

2. **oneself-auth（认证授权服务）**
   - **职责**: 用户认证、Token 生成与验证、权限校验、会话管理
   - **禁止**: 
     - 包含业务逻辑
     - 直接访问业务数据
     - 处理非认证授权相关功能
     - 直接依赖 system 的持久层/表结构
   - **允许**: 
     - 访问用户基础信息（必须通过 `oneself-system-api` 或未来的 `oneself-user-api` 提供的接口获取，禁止直接依赖 system 的持久层/表结构）
     - 调用 Redis 存储 Token
     - 调用数据库存储用户凭证（仅限认证授权相关的凭证数据）

3. **oneself-common-infra（基础设施模块）**
   - **oneself-common-infra-web**: Web 相关工具（请求响应处理、参数校验、异常处理等）
   - **oneself-common-infra-redis**: Redis 操作封装、缓存工具类
   - **oneself-common-infra-db**: 数据库操作封装、MyBatis-Plus 配置、数据源管理
   - **oneself-common-infra-logging**: 日志配置、日志工具类、链路追踪
   - **禁止**: 包含业务逻辑、直接依赖业务模块
   - **允许**: 被所有业务模块依赖，提供通用基础设施能力

4. **oneself-common-feature（功能特性模块）**
   - **oneself-common-feature-core**: 核心工具类（日期处理、字符串处理、加密解密、文件操作等）
   - **oneself-common-feature-security**: 安全相关（权限注解、安全工具类、加密工具等）
   - **禁止**: 包含业务逻辑、直接访问数据库、依赖业务服务
   - **允许**: 被所有业务模块依赖，提供通用功能特性

5. **oneself-service（服务模块）**
   - **职责**: 业务逻辑实现、数据处理、业务规则执行、提供 HTTP/gRPC 接口
   - **禁止**: 接口契约未在 API 模块定义、直接调用其他服务的数据库
   - **允许**: 提供 Controller 实现 HTTP 接口（但接口契约必须在 oneself-service-api 中定义）、调用其他服务的 API 接口、使用公共模块提供的工具、访问自己的数据库

6. **oneself-service-api（API 接口定义模块）**
   - **职责**: 定义服务接口（Feign 接口、DTO、常量等）、接口版本管理、接口兼容性保证
   - **禁止**: 包含业务逻辑实现、直接访问数据库
   - **允许**: 定义接口契约、DTO 对象、常量定义、接口文档注解
   - **接口兼容性规则**:
     1. **不允许破坏性变更**: 字段删除、字段改名、字段语义改变属于破坏性变更，禁止在现有版本中进行
     2. **破坏性变更必须走新版本**: 破坏性变更必须通过新版本包名（如 `v1`、`v2`）或新接口路径（如 `/api/v1/users`、`/api/v2/users`）实现
     3. **DTO 新增字段必须向后兼容**: DTO 新增字段必须保持向后兼容，字段必须提供默认值或设置为可空（`@Nullable`），确保旧版本客户端仍能正常使用
     4. **版本管理策略**: 
        - 使用包名版本：`com.oneself.system.api.v1`、`com.oneself.system.api.v2`
        - 或使用路径版本：`/api/v1/users`、`/api/v2/users`
        - 同一服务内应保持一致的版本管理策略

**跨模块交互规则**:

1. **服务间调用**: 
   - **优先使用**: 通过 `oneself-service-api` 中定义的 Feign 接口进行服务间调用
   - **允许例外**: 以下场景允许直接 HTTP 调用
     - 对接第三方系统（支付、短信、邮件等外部服务）
     - 调用网关或外部系统（非业务服务）
     - 迁移、应急排障等特殊场景（需满足收口机制要求，见下方）
   - **禁止**: 业务服务之间直接使用 RestTemplate/OkHttp 等原生 HTTP 客户端进行调用（应使用 Feign）
   - **应急排障收口机制**: 应急排障场景下的直接 HTTP 调用必须满足以下强约束：
     - 必须在代码中添加 TODO 注释：`// TODO(oneself): remove direct http call before <date>`，其中 `<date>` 为具体日期（不超过一个迭代周期）
     - 必须关联 Issue 链接，说明应急原因和回收计划
     - 代码审查时必须要求在一个迭代内回收成 Feign 接口
     - 禁止将应急直连作为长期方案，必须在指定日期前完成迁移

2. **公共模块使用**: 
   - 业务模块可以依赖 `oneself-common-*` 模块
   - 公共模块之间允许单向依赖：`oneself-common-infra-*` 可以依赖 `oneself-common-feature-*`，但 `oneself-common-feature-*` 禁止依赖 `oneself-common-infra-*`
   - **示例**: `oneself-common-infra-web` 可以使用 `oneself-common-feature-core` 的 R、错误码、异常体系；`oneself-common-infra-logging` 可以使用 `oneself-common-feature-core` 的 TraceId 常量

3. **数据库访问**: 每个服务只能访问自己的数据库，禁止跨服务直接访问数据库

4. **配置管理**: 统一使用 Nacos Config 进行配置管理，禁止硬编码配置

5. **依赖方向**: 依赖方向必须单向，禁止循环依赖
   - `oneself-service` → `oneself-service-api` → `oneself-common-*`
   - `oneself-gateway` → `oneself-auth` → `oneself-common-*`
   - `oneself-common-infra-*` → `oneself-common-feature-*`（允许单向依赖）

**模块依赖矩阵**:

以下表格清晰定义了各模块的依赖规则，便于快速查阅和验证：

| 模块 | 可依赖 | 禁止依赖 |
|------|--------|----------|
| `oneself-gateway` | `oneself-common-*`<br>`oneself-auth-api`（可选） | `oneself-service` / `oneself-service-api`（默认禁止） |
| `oneself-auth` | `oneself-common-*`<br>`oneself-system-api` | `oneself-system` 持久层<br>其他 `oneself-service` |
| `oneself-service` | `oneself-common-*`<br>自己的 `oneself-service-api` | 其他 `oneself-service` 持久层 |
| `oneself-service-api` | `oneself-common-feature-core` | `oneself-common-infra-*`<br>Spring/Web/DB/Redis 实现层 |
| `oneself-common-infra-*` | `oneself-common-feature-*`（允许单向） | 任何业务模块 |
| `oneself-common-feature-*` | `oneself-common-feature-core`（可依赖） | `oneself-common-infra-*`<br>任何业务模块 |

**说明**:
- **可依赖**: 该模块允许依赖的模块列表
- **禁止依赖**: 该模块禁止依赖的模块列表，违反此规则将导致架构问题
- **单向依赖**: `oneself-common-infra-*` 可以依赖 `oneself-common-feature-*`，但反向禁止
- **持久层**: 指数据库访问层（Mapper、Entity 等），禁止跨服务直接访问

**职责边界验证**:

- 代码审查时检查模块依赖关系，确保符合依赖方向规则
- 检查是否有跨模块直接访问数据库的情况
- 检查业务服务间调用是否优先使用 Feign 接口，直接 HTTP 调用是否有合理原因
- 检查应急排障场景下的直接 HTTP 调用是否满足收口机制要求（TODO 注释、Issue 链接、回收计划）
- 检查服务接口契约是否在 oneself-service-api 中定义
- 检查 auth 服务访问用户基础信息是否通过 API 接口，禁止直接依赖 system 的持久层/表结构
- 检查 service-api 中的接口变更是否符合兼容性规则（禁止破坏性变更、新增字段必须向后兼容）
- 使用依赖分析工具（如 Maven Dependency Plugin）检查循环依赖
- 确保公共模块不依赖业务模块
- 验证 infra 模块对 feature 模块的依赖是否符合单向依赖规则

**理由**: 
- 明确的职责边界有助于代码组织和团队协作
- 防止模块间耦合，提高代码可维护性和可测试性
- 便于模块独立开发、测试和部署
- 符合单一职责原则和依赖倒置原则

### 原则 15: SQL 目录管理规范

**规则**: 所有涉及数据库的服务，必须在服务目录下建立 `sql` 目录，保存相关的 SQL 语句，并根据版本创建子目录，记录每个版本的 SQL 变更。

**具体要求**:

1. **目录结构**:
   - 每个涉及数据库的服务必须在服务根目录下创建 `sql` 目录
   - `sql` 目录下必须按版本创建子目录，版本号使用语义化版本格式（如 `v1.0.0`、`v1.1.0`）
   - 每个版本子目录下保存该版本的所有 SQL 变更脚本
   - **目录结构示例**:
     ```
     oneself-auth/
     ├── sql/
     │   ├── v1.0.0/
     │   │   ├── 001_create_user_table.sql
     │   │   ├── 002_create_role_table.sql
     │   │   └── README.md
     │   ├── v1.1.0/
     │   │   ├── 001_add_user_email_column.sql
     │   │   └── README.md
     │   └── README.md
     ```

2. **SQL 文件命名规范**:
   - SQL 文件使用数字前缀（如 `001_`、`002_`）确保执行顺序
   - 文件名使用小写字母、数字和下划线，采用描述性命名
   - 命名格式: `{序号}_{操作类型}_{对象名称}.sql`
   - **操作类型示例**: `create`、`alter`、`drop`、`insert`、`update`、`migrate`
   - **示例**: `001_create_user_table.sql`、`002_alter_user_add_email.sql`、`003_migrate_user_data.sql`

3. **版本目录要求**:
   - 每个版本子目录必须包含 `README.md` 文件，说明该版本的 SQL 变更内容
   - `README.md` 必须包含：
     - 版本号
     - 变更日期
     - 变更说明：列出所有 SQL 变更的目的和影响
     - 执行顺序：说明 SQL 文件的执行顺序和依赖关系
     - 回滚说明：如需要，说明如何回滚该版本的变更

4. **SQL 文件内容要求**:
   - 每个 SQL 文件必须包含注释，说明变更目的、影响范围、执行条件
   - 必须包含事务控制（BEGIN/COMMIT），确保原子性
   - 必须包含错误处理（如适用）
   - 必须包含版本标识注释（如 `-- Version: v1.0.0`）

5. **适用范围**:
   - **必须创建**: 所有涉及数据库操作的服务（如 `oneself-auth`、`oneself-system` 等业务服务）
   - **不需要创建**: 不涉及数据库的模块（如 `oneself-gateway`、`oneself-common-infra-web` 等）

6. **与数据库迁移工具的关系**:
   - `sql` 目录用于保存 SQL 脚本的历史记录和文档
   - 可以与 Flyway 或 Liquibase 配合使用，但 `sql` 目录是必需的文档和版本管理目录
   - Flyway/Liquibase 的迁移脚本可以引用 `sql` 目录中的脚本，或保持独立（但 `sql` 目录必须存在）

**理由**: 
- SQL 脚本的版本化管理便于追踪数据库变更历史
- 按版本组织 SQL 脚本有助于理解数据库演进过程
- 统一的目录结构提高团队协作效率
- 便于数据库迁移、回滚和问题排查
- 符合数据库变更管理的最佳实践

**验证**:
- 代码审查时检查涉及数据库的服务是否包含 `sql` 目录
- 检查 `sql` 目录是否按版本组织
- 检查每个版本子目录是否包含 `README.md` 文件
- 检查 SQL 文件命名是否符合规范
- 检查 SQL 文件是否包含必要的注释和事务控制
- CI/CD 流水线可以添加检查，确保涉及数据库的服务都有 `sql` 目录

### 原则 16: 数据库实体继承规范

**规则**: 所有与数据库表对应的实体类必须继承 `oneself-common-infra-db` 中的 `BaseEntity`。

**具体要求**:

- Entity 必须继承 `com.oneself.common.infra.db.entity.BaseEntity`
- 禁止在实体类中重复定义审计字段与逻辑删除字段（`deleted`、`createTime`、`updateTime`、`createBy`、`updateBy`）
- 若业务实体需要自定义审计字段名称，必须仍继承 `BaseEntity`，并通过注解明确映射差异

**理由**:
- 统一审计字段和逻辑删除行为，减少重复代码
- 确保通用字段自动填充逻辑一致，降低维护成本
- 便于在公共层集中治理数据审计规范

**验证**:
- 代码审查时检查所有 Entity 是否继承 `BaseEntity`
- 检查实体类是否重复定义审计字段或逻辑删除字段

### 原则 17: 微服务 Dockerfile 规范

**规则**: 每个可独立启动的微服务模块必须创建对应的 Dockerfile，分为编译阶段 Dockerfile 和运行阶段 Dockerfile。

**具体要求**:

1. **目录结构**:
   - 每个可启动微服务必须在模块根目录下创建 `docker` 目录
   - `docker` 目录下必须包含两个 Dockerfile：
     - `Dockerfile.build`：编译阶段 Dockerfile，用于构建项目
     - `Dockerfile.run`：运行阶段 Dockerfile，用于运行服务
   - **目录结构示例**:
     ```
     oneself-gateway/
     ├── docker/
     │   ├── Dockerfile.build
     │   └── Dockerfile.run
     ├── src/
     └── pom.xml
     
     oneself-auth/
     ├── docker/
     │   ├── Dockerfile.build
     │   └── Dockerfile.run
     ├── src/
     └── pom.xml
     ```

2. **编译阶段 Dockerfile (Dockerfile.build)**:
   - 用于构建项目，生成可执行的 JAR 文件
   - 必须使用指定的 JDK 21 基础镜像
   - 必须包含 Maven 或 Gradle 构建环境
   - 输出产物为可执行的 JAR 文件

3. **运行阶段 Dockerfile (Dockerfile.run)**:
   - 用于运行服务，应使用轻量级的运行时镜像
   - 必须使用指定的 JRE 21 或 JDK 21 运行时基础镜像
   - 不包含构建工具，仅包含运行时依赖
   - 必须配置合理的 JVM 参数
   - 必须暴露服务端口
   - 建议配置健康检查（HEALTHCHECK）

4. **适用范围**:
   - **必须创建**: 所有可独立启动的微服务（如 `oneself-gateway`、`oneself-auth`、`oneself-system` 等）
   - **不需要创建**: 不可独立启动的模块（如 `oneself-common-*`、`oneself-service-api` 等公共模块和 API 定义模块）

5. **命名规范**:
   - 编译阶段 Dockerfile 命名为 `Dockerfile.build`
   - 运行阶段 Dockerfile 命名为 `Dockerfile.run`
   - 如需支持多种构建场景，可添加后缀，如 `Dockerfile.build.dev`、`Dockerfile.run.prod`

**理由**: 
- 分离编译和运行阶段符合 Docker 最佳实践（多阶段构建理念）
- 运行阶段使用轻量级镜像，减小镜像体积，提高部署效率
- 统一的 Dockerfile 结构便于 CI/CD 流水线配置
- 便于本地开发和生产环境的一致性管理

**验证**:
- 代码审查时检查可启动微服务是否包含 `docker` 目录
- 检查 `docker` 目录是否包含 `Dockerfile.build` 和 `Dockerfile.run`
- 检查 Dockerfile 是否符合命名规范和内容要求
- CI/CD 流水线可以添加检查，确保所有可启动微服务都有对应的 Dockerfile

## 质量保证原则

### 原则 18: 单元测试要求

**规则**: 核心业务逻辑和公共方法必须编写单元测试，测试覆盖率不低于 70%。

**AI 辅助开发规则**: 新增代码时，非必要情况下不允许自动生成单元测试。

**具体要求**:
- AI 辅助开发时，默认不生成单元测试代码
- 仅在以下情况下生成单元测试：
  - 用户明确要求生成单元测试
  - 修改的是核心业务逻辑或公共方法，且用户确认需要测试
  - 修复 Bug 时需要添加回归测试
- 避免生成冗余或低价值的测试代码

**理由**: 
- 单元测试确保代码质量，减少回归问题，提高重构信心
- 避免自动生成大量低价值测试，保持代码仓库整洁
- 让开发者主动决定测试范围，提高测试的针对性和有效性

### 原则 19: 代码规范检查

**规则**: 代码必须通过 Checkstyle、PMD、SpotBugs 等静态代码分析工具检查。

**理由**: 统一的代码风格和规范提高代码可读性和可维护性。

### 原则 20: 对象转换规范（Entity/DTO/VO）

**规则**: 禁止手写逐字段赋值的对象转换代码（如 Entity → VO、Entity → DTO、DTO → VO）。必须优先使用 BeanUtils（如 Spring `BeanUtils.copyProperties`）、MapStruct 或项目已选定的其它转换框架。

**具体要求**:

- Entity、DTO、VO 之间的转换必须使用 `BeanUtils.copyProperties`、MapStruct 或等效框架完成，禁止在业务代码中手写大量 `vo.setXxx(entity.getXxx())` 形式的赋值。
- 若目标对象与源对象字段名、类型一致，优先使用 `BeanUtils.copyProperties(source, target)`；若有字段名或类型差异、或需忽略部分字段，可使用 MapStruct 或自定义拷贝工具并在注释中说明。
- 转换逻辑集中放在工具方法或 Mapper 接口中，避免在 Service 中散落手写转换。

**理由**:
- 手写逐字段转换易遗漏字段、难以与实体变更同步，且重复代码多。
- 使用 BeanUtils/MapStruct 可减少重复、降低出错率，便于统一维护。

**验证**:
- 代码审查时检查 Entity/VO/DTO 转换是否使用 BeanUtils 或 MapStruct，禁止出现大段手写 setXxx(getXxx()) 的转换方法。

### 原则 21: 参数与空值处理规范

**规则**: 对传入参数的判空、校验应尽量使用断言工具（如 Spring `Assert`、Guava `Preconditions`、项目统一 Assert 工具）或 `Optional`，避免冗长的 if-else 分支判断。

**具体要求**:

- 参数非空、状态合法等前置条件校验，优先使用 `Assert.notNull(obj, "message")`、`Assert.hasText(str, "message")` 或项目约定的断言工具，在违反时统一抛出异常。
- 对“可能为空”的语义表达，优先使用 `Optional` 或 `Optional.ofNullable(...).orElse(...)`，减少多层 `if (x == null) { ... } else { ... }` 嵌套。
- 在保持可读性的前提下，避免仅做判空后逐分支赋默认值的冗长 if-else 链，可结合 Optional 或断言简化。

**理由**:
- 断言可集中表达前置条件、统一异常类型与消息，提高可读性和可维护性。
- Optional 明确表达“可能为空”，减少 NPE 与分支嵌套，符合现代 Java 实践。

**验证**:
- 代码审查时检查参数校验与空值处理是否优先使用 Assert 或 Optional，避免不必要的多层 if-else 判空分支。

## 治理规则

### 版本管理

**版本号规则**: 遵循语义化版本（SemVer）规范：`MAJOR.MINOR.PATCH`

- **MAJOR**: 不兼容的 API 修改或重大架构变更
- **MINOR**: 向后兼容的功能新增
- **PATCH**: 向后兼容的问题修复

**宪法版本更新规则**:
- **MAJOR**: 移除或重新定义原则，导致向后不兼容的治理变更
- **MINOR**: 新增原则或章节，或显著扩展现有原则的指导内容
- **PATCH**: 澄清说明、措辞修正、非语义性改进

### 修订程序

1. **提案**: 任何团队成员可以提出宪法修订提案，说明修订理由和影响范围
2. **讨论**: 在团队会议或代码审查中讨论提案，评估对现有代码和流程的影响
3. **批准**: 需要至少 2 名核心成员批准，重大修订需要团队一致同意
4. **更新**: 更新宪法文件，更新版本号和最后修订日期
5. **传播**: 更新相关模板文件（plan-template.md、spec-template.md、tasks-template.md 等），确保一致性
6. **通知**: 通知所有团队成员，更新项目文档

### 合规审查

- **代码审查**: 每次 Pull Request 必须检查是否符合宪法原则
- **定期审查**: 每季度进行一次宪法合规性审查，识别违反原则的代码和技术债务
- **工具检查**: 使用自动化工具（CI/CD）检查技术栈版本、代码规范、测试覆盖率等
- **文档更新**: 宪法修订后，相关技术文档和开发指南必须同步更新

### 例外处理

在特殊情况下（如紧急修复、技术限制），可以申请临时例外，但必须：
1. 在代码注释中明确说明例外原因和预期解决时间
2. 在项目 Issue 中记录例外情况
3. 在下次合规审查中评估是否可以将例外情况标准化或消除

## 附录

### 参考资源

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [RESTful API 设计指南](https://restfulapi.net/)
- [语义化版本规范](https://semver.org/lang/zh-CN/)

### 变更历史

| 版本 | 日期 | 修订内容 | 修订人 |
|------|------|----------|--------|
| 1.0.0 | 2026-02-04 | 根据根目录 constitution.md（原 Atlas 项目宪法）生成 oneself 项目宪法，统一项目名称与模块/包前缀为 oneself | 系统 |
| 1.1.0 | 2026-02-06 | 原则 6：分页查询接口必须使用公共 PageReq，且在分页参数形式上不必遵守 RESTful 设计（允许 POST+PageReq 等） | 系统 |

---

**注意**: 本宪法是项目的根本性指导文档，所有开发活动必须遵循本宪法的规定。如有疑问或建议，请通过修订程序提出。
