# oneself-common 模块架构分析报告

## 一、当前模块结构

### 现有模块列表

1. **oneself-common-core** - 公共核心模块
2. **oneself-common-redis** - Redis 模块
3. **oneself-common-jdbc** - JDBC 模块
4. **oneself-common-mongodb** - MongoDB 模块
5. **oneself-common-elasticsearch** - Elasticsearch 模块
6. **oneself-common-security** - 安全模块
7. **oneself-common-sensitive** - 数据脱敏模块
8. **oneself-common-swagger** - Swagger 模块
9. **oneself-common-utils** - 工具类模块

## 二、规则要求

### 分层架构要求

1. **common-core 层（最底层）**

   - 职责：通用工具类、基础对象、统一返回结构、通用异常和抽象模型
   - 依赖规则：不应依赖任何其他 common 或业务模块

2. **common-infra 层（中间层）**

   - 职责：封装基础设施和中间件能力
   - 依赖规则：只允许依赖 common-core，不得反向依赖更高层模块
   - 命名规范：应使用 `common-infra-xxx` 格式

3. **common-feature 层（最上层）**
   - 职责：提供安全、日志、幂等等横切能力
   - 依赖规则：可以依赖 common-core 和 common-infra，但绝对不能依赖任何业务模块
   - 命名规范：应使用 `common-feature-xxx` 格式

## 三、问题分析

### ❌ 问题 1：模块命名不符合规范

**当前命名：**

- `oneself-common-redis` → 应为 `oneself-common-infra-redis`
- `oneself-common-jdbc` → 应为 `oneself-common-infra-jdbc`
- `oneself-common-mongodb` → 应为 `oneself-common-infra-mongodb`
- `oneself-common-elasticsearch` → 应为 `oneself-common-infra-elasticsearch`
- `oneself-common-security` → 应为 `oneself-common-feature-security`
- `oneself-common-sensitive` → 应为 `oneself-common-feature-sensitive`
- `oneself-common-swagger` → 应为 `oneself-common-feature-swagger`
- `oneself-common-utils` → 应为 `oneself-common-feature-utils`（或考虑合并到 core）

**影响：** 命名不规范导致分层不清晰，不利于维护和理解架构。

---

### ❌ 问题 2：违反分层依赖规则 - common-jdbc 依赖 common-security

**问题描述：**

- `oneself-common-jdbc`（infra 层）依赖了 `oneself-common-security`（feature 层）
- 这违反了"infra 层不得反向依赖更高层模块"的规则

**证据：**

```xml
<!-- oneself-common-jdbc/pom.xml -->
<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-common-security</artifactId>
    <version>1.0.0</version>
</dependency>
```

**代码使用：**

```java
// oneself-common-jdbc/src/main/java/com/oneself/handler/OneselfMetaObjectHandler.java
import com.oneself.utils.SecurityUtils;
// ...
private final SecurityUtils securityUtils;
```

**影响：** 形成了循环依赖风险，破坏了分层架构的单向依赖原则。

**解决方案：**

1. 将 `OneselfMetaObjectHandler` 中对 `SecurityUtils` 的依赖改为可选依赖（通过接口或回调）
2. 或者将 `OneselfMetaObjectHandler` 移到 `common-feature` 层
3. 或者重构 `SecurityUtils.getCurrentUsername()` 为可选的，允许注入默认值

---

### ⚠️ 问题 3：common-security 缺少对 common-core 的显式依赖

**问题描述：**

- `oneself-common-security` 使用了 `common-core` 中的类：
  - `OneselfException`（异常类）
  - `JacksonUtils`（工具类）
- 但 `pom.xml` 中没有显式声明对 `common-core` 的依赖

**证据：**

```java
// SecurityUtils.java
import com.oneself.exception.OneselfException;  // 来自 common-core
import com.oneself.utils.JacksonUtils;  // 来自 common-core
```

**影响：** 虽然可能通过 `common-redis` 间接依赖，但这不是好的实践，应该显式声明。

**解决方案：**
在 `oneself-common-security/pom.xml` 中添加对 `oneself-common-core` 的依赖。

---

### ⚠️ 问题 4：common-elasticsearch 未依赖 common-core

**问题描述：**

- `oneself-common-elasticsearch` 没有依赖 `common-core`
- 虽然当前代码不需要，但为了保持 infra 层的一致性，应该依赖 `common-core`

**影响：** 如果将来需要使用 `common-core` 的功能（如异常、工具类），需要再添加依赖。

**建议：** 为了保持架构一致性，建议添加对 `common-core` 的依赖。

---

### ✅ 符合规则的部分

1. **common-core** 正确：只依赖 Spring Boot，不依赖其他 common 模块 ✓
2. **common-redis** 正确：只依赖 `common-core` ✓
3. **common-mongodb** 正确：只依赖 `common-core` ✓
4. **common-security** 正确：依赖 `common-redis`（feature 层可以依赖 infra 层）✓
5. **common-sensitive** 正确：只依赖 `common-core` ✓
6. **common-utils** 正确：只依赖 `common-core` ✓
7. **common-swagger** 正确：没有依赖其他 common 模块 ✓

## 四、依赖关系图

### 当前依赖关系

```
common-core (最底层)
  ↑
  ├── common-redis (infra)
  ├── common-mongodb (infra)
  ├── common-elasticsearch (infra)
  ├── common-jdbc (infra) ──→ common-security (feature) ❌ 违反规则
  ├── common-utils (feature)
  ├── common-sensitive (feature)
  ├── common-swagger (feature)
  └── common-security (feature) ──→ common-redis (infra) ✓
```

### 期望的依赖关系

```
common-core (最底层)
  ↑
  ├── common-infra-redis
  ├── common-infra-mongodb
  ├── common-infra-elasticsearch
  └── common-infra-jdbc
      ↑
      ├── common-feature-security ──→ common-infra-redis
      ├── common-feature-utils
      ├── common-feature-sensitive
      └── common-feature-swagger
```

## 五、修复建议优先级

### 🔴 高优先级（必须修复）

1. **修复 common-jdbc 对 common-security 的依赖**
   - 重构 `OneselfMetaObjectHandler`，移除对 `SecurityUtils` 的直接依赖
   - 或将其移到 feature 层

### 🟡 中优先级（建议修复）

2. **添加 common-security 对 common-core 的显式依赖**
3. **重命名模块以符合命名规范**
   - 需要修改所有引用这些模块的地方

### 🟢 低优先级（可选）

4. **添加 common-elasticsearch 对 common-core 的依赖**（保持一致性）

## 六、业务逻辑检查

✅ **未发现业务逻辑混入**

- 通过代码检查，未发现 common 模块依赖业务模块（oneself-service、oneself-service-api）
- common 模块内容均为横向能力，符合规则要求

## 七、总结

当前 `oneself-common` 模块整体架构基本符合规则要求，但存在以下主要问题：

1. **命名不规范**：模块命名未体现分层（infra/feature）
2. **依赖违规**：`common-jdbc` 反向依赖 `common-security`，违反分层原则
3. **依赖缺失**：`common-security` 缺少对 `common-core` 的显式依赖

建议优先修复依赖违规问题，然后逐步规范命名和补齐依赖声明。
