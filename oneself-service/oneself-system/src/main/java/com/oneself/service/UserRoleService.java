package com.oneself.service;

import com.oneself.model.vo.RoleVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.service
 * interfaceName UserRoleService
 * description 用户-角色关联服务接口
 * version 1.0
 */
public interface UserRoleService {

    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 分配是否成功
     */
    boolean assignRoles(@Valid @NotBlank String userId, @Valid @NotEmpty List<String> roleIds);

    /**
     * 根据用户ID获取用户所拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表 VO
     */
    List<RoleVO> listRolesByUserId(@Valid @NotBlank String userId);

    /**
     * 删除用户下的所有角色关联
     *
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean deleteByUserId(@Valid @NotBlank String userId);

    /**
     * 删除用户指定角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 删除是否成功
     */
    boolean deleteByUserIdAndRoleIds(@Valid @NotBlank String userId, @Valid @NotEmpty List<String> roleIds);
}