package com.oneself.dept.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.mapper.DeptMapper;
import com.oneself.dept.model.dto.AddDeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.pojo.Dept;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.dept.service.DeptService;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.service.impl
 * className DeptServiceImpl
 * description 部门接口实现类
 * version 1.0
 */
@Slf4j
@Service
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Autowired
    public DeptServiceImpl(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override
    public Integer addDept(AddDeptDTO dto) {
        // 1. 构建部门对象
        Dept dept = new Dept();
        BeanUtils.copyProperties(dto, dept);
        Long parentId = dept.getParentId();
        if (parentId != 0) {
            // 2. 校验上级部门是否存在
            Dept parentDept = deptMapper.selectById(parentId);
            if (ObjectUtils.isEmpty(parentDept)) {
                throw new RuntimeException("上级部门不存在");
            }
        }
        // 3. 校验部门名称是否重复
        checkDeptName(dept);
        // 4. 插入部门
        return deptMapper.insert(dept);
    }

    @Override
    public DeptVO getDept(Long id) {
        Dept dept = deptMapper.selectById(id);
        if (ObjectUtils.isEmpty(dept)) {
            throw new RuntimeException("部门不存在");
        }
        DeptVO deptVO = new DeptVO();
        BeanUtils.copyProperties(dept, deptVO);
        return deptVO;
    }

    @Override
    public Integer updateDept(AddDeptDTO dto) {
        return 0;
    }

    @Override
    public Integer deleteDept(Long id) {
        return 0;
    }

    @Override
    public PageVO<DeptVO> pageList(PageDTO<PageDeptDTO> dto) {
        return PageVO.empty(0L);
    }

    @Override
    public Integer updateStatus(Long id, StatusEnum status) {
        LambdaUpdateWrapper<Dept> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Dept::getId, id)
                .set(Dept::getStatus, status.getCode());

        return deptMapper.update(null, wrapper);
    }

    /**
     * 校验部门名称是否重复
     *
     * @param dept 部门对象
     */
    void checkDeptName(Dept dept) {
        Long id = dept.getId();
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(Dept::getId, id);
        }
        wrapper.eq(Dept::getParentId, dept.getParentId())
                .eq(Dept::getDeptName, dept.getDeptName());
        if (deptMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("部门名称已存在");
        }

    }
}
