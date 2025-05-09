package com.oneself.dept.service;

import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.model.dto.DeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.vo.DeptTreeVO;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;

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
    Integer add(DeptDTO dto);

    DeptVO get(Long id);

    Integer update(Long id, DeptDTO dto);

    Integer delete(List<Long> ids);

    PageVO<DeptVO> page(PageDTO<PageDeptDTO> dto);

    Integer updateStatus(List<Long> ids, StatusEnum status);

    ResponseVO<List<DeptTreeVO>> tree();
}
