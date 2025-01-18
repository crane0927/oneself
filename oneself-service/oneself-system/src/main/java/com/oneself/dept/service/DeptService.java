package com.oneself.dept.service;

import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.model.dto.AddDeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.service
 * interfaceName DeptService
 * description 部门接口
 * version 1.0
 */
public interface DeptService {
    Integer addDept(AddDeptDTO dto);

    DeptVO getDept(Long id);

    Integer updateDept(AddDeptDTO dto);

    Integer deleteDept(Long id);

    PageVO<DeptVO> pageList(PageDTO<PageDeptDTO> dto);

    Integer updateStatus(Long id, StatusEnum status);
}
