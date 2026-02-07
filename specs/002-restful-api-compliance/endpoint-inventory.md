# 接口清单（Endpoint Inventory）

**生成说明**：由任务 T003 产出，供审计报告（T004–T005）使用。  
**范围**：oneself-auth、oneself-system 的 Controller，oneself-system-api 的 Feign 客户端。

**约定**：路径为类级 + 方法级；完整 URL = context-path + 路径（auth: /oneself-auth，system: /oneself-system）。

---

## oneself-auth

| 类/方法 | 路径 | HTTP 方法 | 完整路径（示例） |
|---------|------|-----------|------------------|
| AuthController.login | /auth/login | POST | /oneself-auth/auth/login |
| AuthController.logout | /auth/logout | DELETE | /oneself-auth/auth/logout |
| AuthController.refresh | /auth/refresh | POST | /oneself-auth/auth/refresh |
| AuthController.getCaptcha | /auth/captcha | GET | /oneself-auth/auth/captcha |

---

## oneself-system

| 类/方法 | 路径 | HTTP 方法 | 完整路径（示例） |
|---------|------|-----------|------------------|
| UserController.add | /user | POST | /oneself-system/user |
| UserController.get | /user/{id} | GET | /oneself-system/user/{id} |
| UserController.getUserByName | /user/get/user/by/{name} | GET | /oneself-system/user/get/user/by/{name} |
| UserController.update | /user/{id} | PUT | /oneself-system/user/{id} |
| UserController.delete | /user | DELETE | /oneself-system/user |
| UserController.updateStatus | /user/status/{status} | PUT | /oneself-system/user/status/{status} |
| UserController.page | /user/page | POST | /oneself-system/user/page |
| UserController.listByDeptId | /user/list/by/dept/{deptId} | GET | /oneself-system/user/list/by/dept/{deptId} |
| RoleController.add | /role | POST | /oneself-system/role |
| RoleController.get | /role/{id} | GET | /oneself-system/role/{id} |
| RoleController.update | /role/{id} | PUT | /oneself-system/role/{id} |
| RoleController.delete | /role | DELETE | /oneself-system/role |
| RoleController.updateStatus | /role/status/{status} | PUT | /oneself-system/role/status/{status} |
| RoleController.page | /role/page | POST | /oneself-system/role/page |
| RoleController.listAll | /role/list/all | GET | /oneself-system/role/list/all |
| RoleController.tree | /role/tree | GET | /oneself-system/role/tree |
| DeptController.add | /dept | POST | /oneself-system/dept |
| DeptController.get | /dept/{id} | GET | /oneself-system/dept/{id} |
| DeptController.update | /dept/{id} | PUT | /oneself-system/dept/{id} |
| DeptController.delete | /dept | DELETE | /oneself-system/dept |
| DeptController.updateStatus | /dept/status/{status} | PUT | /oneself-system/dept/status/{status} |
| DeptController.page | /dept/page | POST | /oneself-system/dept/page |
| DeptController.tree | /dept/tree | GET | /oneself-system/dept/tree |
| DeptController.listAll | /dept/list/all | GET | /oneself-system/dept/list/all |
| PermissionController.add | /permission | POST | /oneself-system/permission |
| PermissionController.get | /permission/{id} | GET | /oneself-system/permission/{id} |
| PermissionController.update | /permission/{id} | PUT | /oneself-system/permission/{id} |
| PermissionController.delete | /permission | DELETE | /oneself-system/permission |
| PermissionController.updateStatus | /permission/status/{status} | PUT | /oneself-system/permission/status/{status} |
| PermissionController.page | /permission/page | POST | /oneself-system/permission/page |
| PermissionController.tree | /permission/tree | GET | /oneself-system/permission/tree |
| PermissionController.listByRoleId | /permission/listByRole/{roleId} | GET | /oneself-system/permission/listByRole/{roleId} |
| ConfigurationController.add | /configuration | POST | /oneself-system/configuration |
| ConfigurationController.get | /configuration/{id} | GET | /oneself-system/configuration/{id} |
| ConfigurationController.update | /configuration/{id} | PUT | /oneself-system/configuration/{id} |
| ConfigurationController.delete | /configuration | DELETE | /oneself-system/configuration |
| ConfigurationController.page | /configuration/page | POST | /oneself-system/configuration/page |
| ConstraintController.add | /constraint | POST | /oneself-system/constraint |
| ConstraintController.get | /constraint/{id} | GET | /oneself-system/constraint/{id} |
| ConstraintController.update | /constraint/{id} | PUT | /oneself-system/constraint/{id} |
| ConstraintController.delete | /constraint | DELETE | /oneself-system/constraint |
| ConstraintController.updateStatus | /constraint/status/{status} | PUT | /oneself-system/constraint/status/{status} |
| ConstraintController.page | /constraint/page | POST | /oneself-system/constraint/page |
| UserRoleController.assignRoles | /userRole/assign | POST | /oneself-system/userRole/assign |
| UserRoleController.listRolesByUserId | /userRole/listByUser/{userId} | GET | /oneself-system/userRole/listByUser/{userId} |
| UserRoleController.deleteByUserId | /userRole/deleteByUser/{userId} | DELETE | /oneself-system/userRole/deleteByUser/{userId} |
| UserRoleController.deleteByUserIdAndRoleIds | /userRole/deleteByUserAndRoles/{userId} | DELETE | /oneself-system/userRole/deleteByUserAndRoles/{userId} |
| RolePermissionController.assignPermissions | /roles/{roleId}/permissions | POST | /oneself-system/roles/{roleId}/permissions |
| RolePermissionController.listPermissionsByRoleId | /roles/{roleId}/permissions | GET | /oneself-system/roles/{roleId}/permissions |
| RolePermissionController.deleteByRoleId | /roles/{roleId}/permissions | DELETE | /oneself-system/roles/{roleId}/permissions |
| RolePermissionController.deleteByRoleIdAndPermIds | /roles/{roleId}/permissions/batch | DELETE | /oneself-system/roles/{roleId}/permissions/batch |

---

## oneself-system-api（Feign 客户端，调用 system 服务）

| 接口/方法 | 路径 | HTTP 方法 | 说明 |
|-----------|------|-----------|------|
| UserClient.getUserByName | /get/user/by/{name} | GET | 与 UserController.getUserByName 对应，路径一致 |

---

**合计**：4（auth）+ 50（system Controller）+ 1（system-api Feign，与 UserController.getUserByName 同路径）= 55 条记录，54 个唯一端点。
