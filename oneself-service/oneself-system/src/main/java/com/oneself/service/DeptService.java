package com.oneself.service;

import com.oneself.model.dto.DeptDTO;
import com.oneself.req.PageReq;
import com.oneself.model.dto.DeptQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.DeptTreeVO;
import com.oneself.model.vo.DeptVO;
import com.oneself.resp.PageResp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.service
 * interfaceName DeptService
 * description 部门接口
 * version 1.0
 */
public interface DeptService {

    /**
     * 新增部门
     *
     * @param dto 部门信息 DTO，包含部门名称、父部门ID、排序序号等
     * @return 新增部门的ID
     */
    String add(@Valid DeptDTO dto);

    /**
     * 根据ID查询部门信息
     *
     * @param id 部门ID
     * @return 部门信息 VO
     */
    DeptVO get(@Valid @NotBlank String id);

    /**
     * 更新部门信息
     *
     * @param id  部门ID
     * @param dto 部门信息 DTO，包含更新后的部门名称、父部门ID、排序序号等
     * @return 更新是否成功
     */
    boolean update(@Valid @NotBlank String id, @Valid DeptDTO dto);

    /**
     * 批量删除部门（逻辑删除）
     *
     * @param ids 部门ID列表
     * @return 删除是否成功
     */
    boolean delete(@Valid @NotEmpty List<String> ids);

    /**
     * 批量更新部门状态
     *
     * @param ids    部门ID列表
     * @param status 部门状态（启用/禁用）
     * @return 状态更新是否成功
     */
    boolean updateStatus(@Valid @NotEmpty List<String> ids, @Valid StatusEnum status);

    /**
     * 分页查询部门
     *
     * @param dto 分页查询 DTO，包含页码、每页大小及查询条件
     * @return 分页结果 PageVO<DeptVO>
     */
    PageResp<DeptVO> page(@Valid PageReq<DeptQueryDTO> dto);

    /**
     * 查询部门树形结构
     * <p>
     * 用于前端展示层级结构的部门树
     * </p>
     *
     * @return 部门树列表
     */
    List<DeptTreeVO> tree();


    /**
     * 查询所有部门列表
     *
     * @return 所有部门信息列表
     */
    List<DeptVO> listAll();
}
