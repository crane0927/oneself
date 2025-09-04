package com.oneself.service;

import com.oneself.model.dto.DeptDTO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.dto.PageDeptDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.DeptTreeVO;
import com.oneself.model.vo.DeptVO;
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
    String add(DeptDTO dto);

    DeptVO get(String id);

    boolean update(String id, DeptDTO dto);

    boolean delete(List<String> ids);

    PageVO<DeptVO> page(PageDTO<PageDeptDTO> dto);

    boolean updateStatus(List<String> ids, StatusEnum status);

    List<DeptTreeVO> tree();
}
