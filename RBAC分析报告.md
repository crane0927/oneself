# oneself-system 服务 RBAC 理论分析报告

## 一、RBAC 理论概述

RBAC（Role-Based Access Control，基于角色的访问控制）是一种访问控制模型，其核心思想是：
- **用户（User）** → **角色（Role）** → **权限（Permission）**
- 用户通过分配角色来获得权限，而不是直接分配权限
- 权限与角色关联，角色与用户关联

### RBAC 核心组件
1. **用户（User）**：系统的使用者
2. **角色（Role）**：权限的集合，代表一组权限
3. **权限（Permission）**：对资源的操作许可
4. **用户-角色关联（User-Role）**：多对多关系
5. **角色-权限关联（Role-Permission）**：多对多关系

---

## 二、系统架构分析

### 2.1 数据模型设计

#### 核心实体表

**1. sys_user（用户表）**
```12:84:oneself-service/oneself-system/src/main/java/com/oneself/model/pojo/User.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 性别(0-未知,1-男,2-女)
     */
    @TableField("sex")
    private SexEnum sex;

    /**
     * 用户类型(0-管理员,1-普通用户)
     */
    @TableField("type")
    private UserTypeEnum type;

    /**
     * 所属部门ID
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}
```

**2. sys_role（角色表）**
```20:52:oneself-service/oneself-system/src/main/java/com/oneself/model/pojo/Role.java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 角色编码
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}
```

**3. sys_permission（权限表）**
```21:77:oneself-service/oneself-system/src/main/java/com/oneself/model/pojo/Permission.java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 权限编码
     */
    @TableField("perm_code")
    private String permCode;

    /**
     * 权限名称
     */
    @TableField("perm_name")
    private String permName;

    /**
     * 权限描述
     */
    @TableField("description")
    private String description;

    /**
     * 资源类型(menu-菜单,button-按钮,api-接口)
     */
    @TableField("resource_type")
    private ResourceTypeEnum resourceType;

    /**
     * 资源路径
     */
    @TableField("resource_path")
    private String resourcePath;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private StatusEnum status;
}
```

**4. sys_user_role（用户角色关联表）**
```19:39:oneself-service/oneself-system/src/main/java/com/oneself/model/pojo/UserRole.java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class UserRole extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;
}
```

**5. sys_role_permission（角色权限关联表）**
```19:39:oneself-service/oneself-system/src/main/java/com/oneself/model/pojo/RolePermission.java
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermission extends BasePojo {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private String permId;
}
```

### 2.2 关系模型图

```
┌──────────┐         ┌──────────────┐         ┌──────────┐
│   User   │────────│ UserRole     │────────│  Role    │
└──────────┘         └──────────────┘         └──────────┘
                                                      │
                                                      │
                                              ┌──────────────┐
                                              │RolePermission│
                                              └──────────────┘
                                                      │
                                                      │
                                              ┌──────────────┐
                                              │  Permission  │
                                              └──────────────┘
```

**关系说明：**
- **User ↔ Role**：多对多关系，通过 `sys_user_role` 表关联
- **Role ↔ Permission**：多对多关系，通过 `sys_role_permission` 表关联
- **User → Permission**：间接关系，通过角色关联

---

## 三、RBAC 实现分析

### 3.1 ✅ 符合 RBAC 标准的实现

#### 1. 核心关系实现完整

**用户-角色分配**
```44:76:oneself-service/oneself-system/src/main/java/com/oneself/service/impl/UserRoleServiceImpl.java
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysUser", key = "'session:' + #userId")
    public boolean assignRoles(String userId, List<String> roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }

        // 检查角色是否存在
        List<Role> roles = roleMapper.selectByIds(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException("部分角色不存在");
        }

        // 先删除用户现有的角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));

        // 批量插入新的角色关联
        List<UserRole> userRoles = roleIds.stream()
            .map(roleId -> UserRole.builder()
                .userId(userId)
                .roleId(roleId)
                .build())
            .toList();

        // 批量插入
        for (UserRole userRole : userRoles) {
            userRoleMapper.insert(userRole);
        }

        log.info("用户角色分配成功, 用户ID: {}, 角色数量: {}", userId, roleIds.size());
        return true;
    }
```

**角色-权限分配**
```44:76:oneself-service/oneself-system/src/main/java/com/oneself/service/impl/RolePermissionServiceImpl.java
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysRolePermission", key = "#roleId")
    public boolean assignPermissions(String roleId, List<String> permIds) {
        if (ObjectUtils.isEmpty(permIds)) {
            throw new IllegalArgumentException("权限ID列表不能为空");
        }

        // 检查权限是否存在
        List<Permission> permissions = permissionMapper.selectByIds(permIds);
        if (permissions.size() != permIds.size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }

        // 先删除角色现有的权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));

        // 批量插入新的权限关联
        List<RolePermission> rolePermissions = permIds.stream()
            .map(permId -> RolePermission.builder()
                .roleId(roleId)
                .permId(permId)
                .build())
            .toList();

        // 批量插入
        for (RolePermission rolePermission : rolePermissions) {
            rolePermissionMapper.insert(rolePermission);
        }

        log.info("角色权限分配成功, 角色ID: {}, 权限数量: {}", roleId, permIds.size());
        return true;
    }
```

#### 2. 权限查询链路完整

**用户登录时加载权限**
```135:164:oneself-service/oneself-system/src/main/java/com/oneself/service/impl/UserServiceImpl.java
        List<String> roleIds = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId,
                        user.getId()))
                .stream().map(UserRole::getRoleId).toList();
        if (!roleIds.isEmpty()) {
            List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIds));
            vo.setRoleCodes(roles.stream().map(Role::getRoleCode).collect(Collectors.toSet()));

            List<String> permissionIds = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>()
                            .in(RolePermission::getRoleId, roleIds))
                    .stream()
                    .map(RolePermission::getPermId).toList();
            if (!permissionIds.isEmpty()) {
                List<Permission> permissions = permissionMapper.selectList(new LambdaQueryWrapper<Permission>()
                        .in(Permission::getId, permissionIds));
                vo.setPermissionCodes(permissions.stream().map(Permission::getPermCode).collect(Collectors.toSet()));
            }
        }
```

**查询链路：**
1. User → UserRole → Role（获取用户角色）
2. Role → RolePermission → Permission（获取角色权限）
3. 合并所有角色的权限集合

#### 3. 权限验证机制

**注解驱动的权限控制**
- `@RequireLogin`：登录验证
- `@RequireRoles`：角色验证
- `@RequirePermission`：权限验证

**权限验证逻辑**
```148:163:oneself-common/oneself-common-feature/oneself-common-feature-security/src/main/java/com/oneself/utils/SecurityUtils.java
    public void checkPermission(String[] requiredPerms, boolean strict) {
        checkLogin();
        LoginUserBO user = loadUserFromRedis(getCurrentUser().getSessionId());
        if (user == null) {
            if (strict) throw new OneselfException("用户信息不存在");
            return;
        }

        if (UserTypeEnum.ADMIN.equals(user.getType())) return; // 管理员直接放行

        Set<String> userPerms = Optional.ofNullable(user.getPermissionCodes()).orElse(Collections.emptySet());
        Set<String> needPerms = new HashSet<>(Arrays.asList(requiredPerms));
        if (Collections.disjoint(userPerms, needPerms) && strict) {
            throw new OneselfException("操作权限不足");
        }
    }
```

### 3.2 权限模型特点

#### 1. 权限层级结构
- 支持父子权限关系（`parent_id`）
- 支持权限树形结构展示
- 支持多种资源类型：菜单（menu）、按钮（button）、接口（api）

```275:326:oneself-service/oneself-system/src/main/java/com/oneself/service/impl/PermissionServiceImpl.java
    /**
     * 查询权限树
     * <p>
     * 构建权限层级结构（用于前端展示菜单/按钮/接口树）
     * </p>
     *
     * @return 权限树列表
     */
    @Override
    @Cacheable(value = "sysPermission", key = "'tree'")
    public List<PermissionTreeVO> tree() {
        // 查询所有权限
        List<Permission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getStatus, StatusEnum.NORMAL)
                        .orderByAsc(Permission::getSortOrder)
        );

        if (ObjectUtils.isEmpty(permissions)) {
            return List.of();
        }

        // 转换为TreeVO
        List<PermissionTreeVO> treeVOs = permissions.stream()
                .map(permission -> {
                    PermissionTreeVO vo = new PermissionTreeVO();
                    BeanCopyUtils.copy(permission, vo);
                    return vo;
                })
                .toList();

        // 构建id与TreeVO的映射
        Map<String, PermissionTreeVO> idToTreeVOMap = new HashMap<>();
        treeVOs.forEach(vo -> idToTreeVOMap.put(vo.getId(), vo));

        // 构建树结构
        List<PermissionTreeVO> rootNodes = new ArrayList<>();
        for (PermissionTreeVO vo : treeVOs) {
            if (ObjectUtils.isEmpty(vo.getParentId())) {
                // 没有父节点的为顶级节点
                rootNodes.add(vo);
            } else {
                // 找到父节点，并把当前节点加入父节点的children中
                PermissionTreeVO parent = idToTreeVOMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }

        return rootNodes;
    }
```

#### 2. 状态管理
- 用户、角色、权限都支持启用/禁用状态
- 禁用状态不影响数据完整性，只影响权限验证

#### 3. 缓存机制
- 使用 Spring Cache 缓存用户权限信息
- 缓存键设计：
    - `sysUser:session:{userId}` - 用户会话信息
    - `sysUserRole:{userId}` - 用户角色列表
    - `sysRolePermission:{roleId}` - 角色权限列表
    - `sysPermission:tree` - 权限树

---

## 四、RBAC 模型评估

### 4.1 ✅ 符合 RBAC 核心原则

| RBAC 原则 | 实现情况 | 说明 |
|----------|---------|------|
| **用户-角色分离** | ✅ 完全符合 | 用户不直接拥有权限，通过角色间接获得 |
| **角色-权限分离** | ✅ 完全符合 | 权限与角色关联，角色与用户关联 |
| **多对多关系** | ✅ 完全符合 | 用户可拥有多个角色，角色可拥有多个权限 |
| **权限继承** | ⚠️ 部分支持 | 通过 `parent_id` 支持权限层级，但未实现角色继承 |
| **最小权限原则** | ✅ 支持 | 通过精确的权限分配实现 |

### 4.2 RBAC 模型类型

**当前实现：RBAC0（基础 RBAC）**

- ✅ 用户-角色-权限三层模型
- ✅ 多对多关系支持
- ⚠️ 未实现角色继承（RBAC1）
- ⚠️ 未实现约束条件（RBAC2）
- ⚠️ 未实现会话管理（RBAC3）

### 4.3 优势

1. **清晰的权限模型**
    - 符合 RBAC 标准设计
    - 数据模型规范，关系明确

2. **灵活的权限分配**
    - 支持用户多角色
    - 支持角色多权限
    - 支持权限层级结构

3. **良好的性能优化**
    - 使用缓存减少数据库查询
    - 登录时一次性加载用户权限

4. **完善的业务功能**
    - 支持权限树形结构
    - 支持多种资源类型（菜单/按钮/接口）
    - 支持状态管理（启用/禁用）

### 4.4 可改进点

#### 1. ✅ 角色继承（RBAC1）- 已实现

**实现状态：** ✅ 已完成

**实现内容：**

1. **数据库表结构修改**
   - 在 `sys_role` 表中添加 `parent_id` 字段，用于存储父角色ID
   ```sql
   ALTER TABLE sys_role ADD COLUMN parent_id VARCHAR(32);
   ```

2. **实体类修改**
   - `Role` 实体类添加 `parentId` 字段
   - `RoleDTO` 和 `RoleVO` 添加 `parentId` 字段

3. **角色继承逻辑实现**
   - **防止循环继承**：在新增/更新角色时，检查是否形成循环继承
   - **获取父角色链**：`getAllParentRoleIds()` 方法获取角色的所有父角色（包括自身）
   - **获取子角色链**：`getAllChildRoleIds()` 方法获取角色的所有子角色

4. **权限继承实现**
   - **用户权限查询**：查询用户权限时，自动包含所有父角色的权限
   - **角色权限查询**：查询角色权限时，自动包含所有父角色的权限
   - 子角色自动继承父角色的所有权限

5. **角色树查询**
   - 新增 `RoleTreeVO` 用于展示角色树形结构
   - 新增 `tree()` 方法查询角色树，展示角色继承关系
   - 新增 `/role/tree` 接口用于前端展示

**核心实现代码：**

```java
// 获取角色的所有父角色（包括自身）
public Set<String> getAllParentRoleIds(String roleId) {
    Set<String> parentIds = new HashSet<>();
    parentIds.add(roleId); // 包括自身
    
    String currentParentId = roleId;
    Set<String> visited = new HashSet<>(); // 防止无限循环
    
    while (ObjectUtils.isNotEmpty(currentParentId) && !visited.contains(currentParentId)) {
        visited.add(currentParentId);
        Role role = roleMapper.selectById(currentParentId);
        if (ObjectUtils.isEmpty(role) || ObjectUtils.isEmpty(role.getParentId())) {
            break;
        }
        currentParentId = role.getParentId();
        parentIds.add(currentParentId);
    }
    
    return parentIds;
}
```

**权限继承查询示例：**

```java
// 查询用户权限时，考虑角色继承
Set<String> allRoleIds = new HashSet<>();
for (String roleId : roleIds) {
    // 获取该角色的所有父角色（包括自身）
    Set<String> parentRoleIds = roleService.getAllParentRoleIds(roleId);
    allRoleIds.addAll(parentRoleIds);
}

// 查询所有角色（包括继承的角色）的权限
List<String> permissionIds = rolePermissionMapper.selectList(
    new LambdaQueryWrapper<RolePermission>()
        .in(RolePermission::getRoleId, allRoleIds)
)
.stream()
.map(RolePermission::getPermId)
.distinct()
.collect(Collectors.toList());
```

**使用场景：**
- 角色A继承角色B，角色B继承角色C
- 用户拥有角色A时，自动拥有角色B和角色C的所有权限
- 前端可以通过 `/role/tree` 接口查看角色继承关系树

#### 2. ✅ 权限约束（RBAC2）- 已实现

**实现状态：** ✅ 已完成

**实现内容：**

1. **数据库表结构**
   - 创建 `sys_constraint` 表，用于存储约束配置
   - 支持四种约束类型：角色互斥、权限互斥、基数约束、先决条件

2. **约束类型实现**

   **a. 角色互斥约束（ROLE_MUTEX）**
   - 防止某些角色同时分配给同一用户
   - 约束值格式：`{"mutexRoles": ["roleId1", "roleId2"]}`
   - 示例：管理员和普通用户不能同时拥有

   **b. 权限互斥约束（PERM_MUTEX）**
   - 防止某些权限同时拥有
   - 约束值格式：`{"mutexPerms": ["permId1", "permId2"]}`
   - 示例：读写权限互斥

   **c. 基数约束（CARDINALITY）**
   - 限制用户拥有的角色数量
   - 限制角色拥有的权限数量
   - 约束值格式：
     - 用户角色：`{"type": "USER_ROLE", "max": 5}`
     - 角色权限：`{"type": "ROLE_PERM", "max": 100}`

   **d. 先决条件约束（PREREQUISITE）**
   - 用户必须拥有某个角色才能拥有另一个角色
   - 约束值格式：`{"targetRole": "roleId1", "prerequisiteRole": "roleId2"}`

3. **约束检查集成**
   - **用户角色分配**：在 `UserRoleServiceImpl.assignRoles()` 中集成
     - 角色互斥检查
     - 用户角色基数约束检查
     - 角色先决条件约束检查
   - **角色权限分配**：在 `RolePermissionServiceImpl.assignPermissions()` 中集成
     - 权限互斥检查
     - 角色权限基数约束检查

4. **约束管理接口**
   - 提供完整的 CRUD 接口（`ConstraintController`）
   - 支持约束配置的启用/禁用
   - 支持分页查询和条件筛选

**核心实现代码：**

```java
// 角色互斥约束检查
public void checkRoleMutexConstraint(List<String> roleIds) {
    // 查询所有启用的角色互斥约束
    List<Constraint> constraints = constraintMapper.selectList(
        new LambdaQueryWrapper<Constraint>()
            .eq(Constraint::getConstraintType, ConstraintTypeEnum.ROLE_MUTEX)
            .eq(Constraint::getStatus, StatusEnum.NORMAL)
    );
    
    // 解析约束值并检查是否违反约束
    for (Constraint constraint : constraints) {
        Map<String, Object> constraintValue = JacksonUtils.fromJson(
            constraint.getConstraintValue(), Map.class
        );
        List<String> mutexRoles = (List<String>) constraintValue.get("mutexRoles");
        
        // 检查是否有互斥的角色同时存在
        long count = mutexRoles.stream()
            .filter(roleIdSet::contains).count();
        if (count > 1) {
            throw new OneselfException("违反角色互斥约束");
        }
    }
}
```

**约束配置示例：**

1. **角色互斥约束**
   ```json
   {
     "constraintType": "ROLE_MUTEX",
     "constraintName": "管理员与普通用户互斥",
     "constraintValue": "{\"mutexRoles\": [\"admin_role_id\", \"normal_user_role_id\"]}",
     "status": "NORMAL"
   }
   ```

2. **用户角色基数约束**
   ```json
   {
     "constraintType": "CARDINALITY",
     "constraintName": "用户最多5个角色",
     "constraintValue": "{\"type\": \"USER_ROLE\", \"max\": 5}",
     "status": "NORMAL"
   }
   ```

3. **角色先决条件约束**
   ```json
   {
     "constraintType": "PREREQUISITE",
     "constraintName": "高级角色需要基础角色",
     "constraintValue": "{\"targetRole\": \"senior_role_id\", \"prerequisiteRole\": \"basic_role_id\"}",
     "status": "NORMAL"
   }
   ```

**使用场景：**
- 防止权限冲突：确保用户不会同时拥有互斥的角色或权限
- 控制权限范围：限制用户拥有的角色数量和角色拥有的权限数量
- 强制依赖关系：确保用户拥有必要的先决角色才能获得高级角色

#### 3. 缺少会话管理（RBAC3）
**现状：** 权限验证基于用户，不区分会话