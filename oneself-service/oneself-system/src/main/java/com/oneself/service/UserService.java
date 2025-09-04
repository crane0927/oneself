package com.oneself.service;

import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.QueryDTO;
import com.oneself.model.dto.UserDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.PageVO;
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

    UserVO get(@Valid @NotBlank String id);

    String add(@Valid UserDTO dto);

    boolean update(@Valid @NotBlank String id, @Valid UserDTO dto);

    boolean delete(@Valid @NotEmpty List<String> ids);

    boolean updateStatus(@Valid @NotEmpty List<String> ids, @Valid @NotBlank StatusEnum status);

    PageVO<UserVO> page(@Valid PageDTO<QueryDTO> dto);

}
