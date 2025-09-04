package com.oneself.service;

import com.oneself.model.dto.DeptDTO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.QueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.DeptTreeVO;
import com.oneself.model.vo.DeptVO;
import com.oneself.model.vo.PageVO;
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

    String add(@Valid DeptDTO dto);

    DeptVO get(@Valid @NotBlank String id);

    boolean update(@Valid @NotBlank String id, @Valid DeptDTO dto);

    boolean delete(@Valid @NotEmpty List<String> ids);

    boolean updateStatus(@Valid @NotEmpty List<String> ids, @Valid @NotBlank StatusEnum status);

    PageVO<DeptVO> page(@Valid PageDTO<QueryDTO> dto);

    List<DeptTreeVO> tree();
}
