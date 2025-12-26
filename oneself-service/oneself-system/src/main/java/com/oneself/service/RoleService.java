package com.oneself.service;

import com.oneself.model.dto.RoleDTO;
import com.oneself.model.dto.RoleQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.RoleVO;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.service
 * interfaceName RoleService
 * description 角色服务接口
 * version 1.0
 */
public interface RoleService {

    /**
     * 新增角色
     *
     * @param dto 角色信息 DTO，包含角色名称、角色编码、角色描述等
     * @return 新增角色ID
     */
    String add(@Valid RoleDTO dto);

    /**
     * 根据ID查询角色信息
     *
     * @param id 角色ID
     * @return 角色信息 VO
     */
    RoleVO get(@Valid @NotBlank String id);

    /**
     * 更新角色信息
     *
     * @param id  角色ID
     * @param dto 角色信息 DTO，包含更新后的角色名称、角色编码、角色描述等
     * @return 更新是否成功
     */
    boolean update(@Valid @NotBlank String id, @Valid RoleDTO dto);

    /**
     * 批量删除角色（逻辑删除）
     *
     * @param ids 角色ID列表
     * @return 删除是否成功
     */
    boolean delete(@Valid @NotEmpty List<String> ids);

    /**
     * 批量更新角色状态
     *
     * @param ids    角色ID列表
     * @param status 角色状态（启用/禁用）
     * @return 状态更新是否成功
     */
    boolean updateStatus(@Valid @NotEmpty List<String> ids, @Valid @NotBlank StatusEnum status);

    /**
     * 分页查询角色列表
     *
     * @param dto 分页查询 DTO，包含页码、每页大小及查询条件
     * @return 分页结果 PageVO<RoleVO>
     */
    PageResp<RoleVO> page(@Valid PageReq<RoleQueryDTO> dto);

    /**
     * 查询所有角色列表
     *
     * @return 所有角色信息列表
     */
    List<RoleVO> listAll();

    /**
     * 获取角色的所有父角色ID（包括直接父角色和所有祖先角色）
     * 用于权限继承：子角色自动继承父角色的所有权限（RBAC1）
     *
     * @param roleId 角色ID
     * @return 所有父角色ID集合（包括自身）
     */
    java.util.Set<String> getAllParentRoleIds(String roleId);

    /**
     * 查询角色树形结构（RBAC1：支持角色继承）
     * <p>
     * 用于前端展示层级结构的角色树，展示角色继承关系
     * </p>
     *
     * @return 角色树列表
     */
    List<com.oneself.model.vo.RoleTreeVO> tree();
}
