package com.oneself.service;

import com.oneself.req.PageReq;
import com.oneself.model.dto.PermissionDTO;
import com.oneself.model.dto.PermissionQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.resp.PageResp;
import com.oneself.model.vo.PermissionTreeVO;
import com.oneself.model.vo.PermissionVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/5
 * packageName com.oneself.service
 * interfaceName PermissionService
 * description  权限服务接口
 * version 1.0
 */
public interface PermissionService {

    /**
     * 新增权限
     *
     * @param dto 权限信息 DTO，包含权限名称、权限编码、资源类型、资源路径等信息
     * @return 新增权限ID
     */
    String add(@Valid PermissionDTO dto);

    /**
     * 根据ID查询权限信息
     *
     * @param id 权限ID
     * @return 权限信息 VO
     */
    PermissionVO get(@Valid @NotBlank String id);

    /**
     * 更新权限信息
     *
     * @param id  权限ID
     * @param dto 权限信息 DTO，包含更新后的权限名称、权限编码、资源类型、资源路径等
     * @return 更新是否成功
     */
    boolean update(@Valid @NotBlank String id, @Valid PermissionDTO dto);

    /**
     * 批量删除权限（逻辑删除）
     *
     * @param ids 权限ID列表
     * @return 删除是否成功
     */
    boolean delete(@Valid @NotEmpty List<String> ids);

    /**
     * 批量更新权限状态
     *
     * @param ids    权限ID列表
     * @param status 权限状态（启用/禁用）
     * @return 状态更新是否成功
     */
    boolean updateStatus(@Valid @NotEmpty List<String> ids, @Valid @NotBlank StatusEnum status);

    /**
     * 分页查询权限列表
     *
     * @param dto 分页查询 DTO，包含页码、每页大小及查询条件
     * @return 分页结果 PageVO<PermissionVO>
     */
    PageResp<PermissionVO> page(@Valid PageReq<PermissionQueryDTO> dto);

    /**
     * 查询权限树
     * <p>
     * 构建权限层级结构（用于前端展示菜单/按钮/接口树）
     * </p>
     *
     * @return 权限树列表
     */
    List<PermissionTreeVO> tree();

    /**
     * 根据角色ID查询权限列表
     * <p>
     * 获取某个角色下所有分配的权限
     * </p>
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionVO> listByRoleId(@Valid @NotBlank String roleId);
}
