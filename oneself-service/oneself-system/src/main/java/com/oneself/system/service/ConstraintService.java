package com.oneself.system.service;

import com.oneself.common.core.exception.OneselfException;
import com.oneself.system.model.dto.ConstraintDTO;
import com.oneself.system.model.dto.ConstraintQueryDTO;
import com.oneself.common.feature.security.model.enums.StatusEnum;
import com.oneself.system.model.vo.ConstraintVO;
import com.oneself.common.feature.web.req.PageReq;
import com.oneself.common.feature.web.resp.PageResp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/12/25
 * packageName com.oneself.service
 * interfaceName ConstraintService
 * description 约束配置服务接口（RBAC2）
 * version 1.0
 */
public interface ConstraintService {

    /**
     * 新增约束配置
     *
     * @param dto 约束配置 DTO
     * @return 新增约束配置ID
     */
    String add(@Valid ConstraintDTO dto);

    /**
     * 根据ID查询约束配置
     *
     * @param id 约束配置ID
     * @return 约束配置 VO
     */
    ConstraintVO get(@Valid @NotBlank String id);

    /**
     * 更新约束配置
     *
     * @param id  约束配置ID
     * @param dto 约束配置 DTO
     * @return 更新是否成功
     */
    boolean update(@Valid @NotBlank String id, @Valid ConstraintDTO dto);

    /**
     * 批量删除约束配置
     *
     * @param ids 约束配置ID列表
     * @return 删除是否成功
     */
    boolean delete(@Valid @NotEmpty List<String> ids);

    /**
     * 批量更新约束配置状态
     *
     * @param ids    约束配置ID列表
     * @param status 状态
     * @return 更新是否成功
     */
    boolean updateStatus(@Valid @NotEmpty List<String> ids, @Valid @NotBlank StatusEnum status);

    /**
     * 分页查询约束配置
     *
     * @param dto 分页查询 DTO
     * @return 分页结果
     */
    PageResp<ConstraintVO> page(@Valid PageReq<ConstraintQueryDTO> dto);

    /**
     * 检查角色互斥约束（RBAC2）
     *
     * @param roleIds 角色ID列表
     * @throws OneselfException 如果违反约束
     */
    void checkRoleMutexConstraint(List<String> roleIds);

    /**
     * 检查权限互斥约束（RBAC2）
     *
     * @param permIds 权限ID列表
     * @throws OneselfException 如果违反约束
     */
    void checkPermissionMutexConstraint(List<String> permIds);

    /**
     * 检查用户角色基数约束（RBAC2）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @throws OneselfException 如果违反约束
     */
    void checkUserRoleCardinalityConstraint(String userId, List<String> roleIds);

    /**
     * 检查角色权限基数约束（RBAC2）
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     * @throws OneselfException 如果违反约束
     */
    void checkRolePermissionCardinalityConstraint(String roleId, List<String> permIds);

    /**
     * 检查角色先决条件约束（RBAC2）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @throws OneselfException 如果违反约束
     */
    void checkRolePrerequisiteConstraint(String userId, List<String> roleIds);
}

