package com.oneself.dept.service;

import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.model.dto.DeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;

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
    Integer addDept(DeptDTO dto);

    DeptVO getDept(Long id);

    Integer updateDept(Long id, DeptDTO dto);

    Integer deleteDept(List<Long> ids);

    PageVO<DeptVO> pageList(PageDTO<PageDeptDTO> dto);

    Integer updateStatus(List<Long> ids, StatusEnum status);
}
