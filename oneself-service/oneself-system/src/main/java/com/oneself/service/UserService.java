package com.oneself.service;

import com.oneself.req.PageReq;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.dto.UserQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.UserSessionVO;
import com.oneself.resp.PageResp;
import com.oneself.model.vo.UserVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.service
 * interfaceName UserService
 * description 用户接口
 * version 1.0
 */
public interface UserService {

    /**
     * 新增用户
     *
     * @param dto 用户信息 DTO
     * @return 新增用户ID
     */
    String add(@Valid UserDTO dto);

    /**
     * 根据 ID 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息 VO
     */
    UserVO get(@Valid @NotBlank String id);

    /**
     * 查询登录用户
     *
     * @param name 用户名
     * @return 登录用户信息 VO
     */
    UserSessionVO getSessionByName(@Valid @NotBlank String name);

    /**
     * 更新用户信息
     *
     * @param id  用户ID
     * @param dto 用户信息 DTO
     * @return 更新是否成功
     */
    boolean update(@Valid @NotBlank String id, @Valid UserDTO dto);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 删除是否成功
     */
    boolean delete(@Valid @NotEmpty List<String> ids);

    /**
     * 批量更新用户状态
     *
     * @param ids    用户ID列表
     * @param status 用户状态
     * @return 是否成功
     */
    boolean updateStatus(@Valid @NotEmpty List<String> ids, @Valid @NotBlank StatusEnum status);

    /**
     * 分页查询用户
     *
     * @param dto 分页查询 DTO
     * @return 分页结果
     */
    PageResp<UserVO> page(@Valid PageReq<UserQueryDTO> dto);

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    List<UserVO> listByDeptId(@Valid @NotBlank String deptId);

}
