package com.oneself.service;

import com.oneself.model.vo.PermissionVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.service
 * interfaceName RolePermissionService
 * description 角色-权限关联服务接口
 * version 1.0
 */
public interface RolePermissionService {

    /**
     * 给角色分配权限
     *
     * @param roleId 角色ID
     * @param permIds 权限ID列表
     * @return 分配是否成功
     */
    boolean assignPermissions(@Valid @NotBlank String roleId, @Valid @NotEmpty List<String> permIds);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表 VO
     */
    List<PermissionVO> listPermissionsByRoleId(@Valid @NotBlank String roleId);

    /**
     * 删除角色下的所有权限关联
     *
     * @param roleId 角色ID
     * @return 删除是否成功
     */
    boolean deleteByRoleId(@Valid @NotBlank String roleId);

    /**
     * 删除角色指定权限关联
     *
     * @param roleId 角色ID
     * @param permIds 权限ID列表
     * @return 删除是否成功
     */
    boolean deleteByRoleIdAndPermIds(@Valid @NotBlank String roleId, @Valid @NotEmpty List<String> permIds);
}