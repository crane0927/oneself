package com.oneself.dept.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.mapper.DeptMapper;
import com.oneself.dept.model.dto.DeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.pojo.Dept;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.dept.service.DeptService;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Integer addDept(DeptDTO dto) {
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
    public Integer updateDept(Long id, DeptDTO dto) {
        // 1. 构建部门对象
        Dept dept = new Dept();
        BeanUtils.copyProperties(dto, dept);
        dept.setId(id);
        // 2. 校验部门名称是否重复
        checkDeptName(dept);
        Long parentId = dept.getParentId();
        if (parentId != 0) {
            // 3. 校验上级部门是否存在
            Dept parentDept = deptMapper.selectById(parentId);
            if (ObjectUtils.isEmpty(parentDept)) {
                throw new RuntimeException("上级部门不存在");
            }
        }
        // 4. 更新部门
        return deptMapper.updateById(dept);
    }

    @Override
    public Integer deleteDept(List<Long> ids) {
        return 0;
    }

    @Override
    public PageVO<DeptVO> pageList(PageDTO<PageDeptDTO> dto) {
        // 1. 构建查询条件
        PageDeptDTO condition = dto.getCondition();
        // 2. 分页参数
        PageDTO.Pagination pagination = dto.getPagination();
        // 3. 构建分页参数
        Page<Dept> pageRequest = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        // 4. 查询
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<Dept>()
                .like(StringUtils.isNoneBlank(condition.getDeptName()), Dept::getDeptName, condition.getDeptName()) // 部门名称模糊查询
                .like(StringUtils.isNoneBlank(condition.getDeptDesc()), Dept::getDeptDesc, condition.getDeptDesc()) // 部门描述模糊查询
                .like(StringUtils.isNoneBlank(condition.getLeader()), Dept::getLeader, condition.getLeader()) // 部门负责人模糊查询
                .eq(Optional.ofNullable(condition.getStatus()).isPresent(), Dept::getStatus, condition.getStatus()) // 部门状态查询
                .orderByAsc(Dept::getSequence); // 排序

        Page<Dept> deptPage = deptMapper.selectPage(pageRequest, wrapper);
        // 5. 转换分页数据
        return PageVO.convert(deptPage, dept -> {
            DeptVO deptVO = new DeptVO();
            BeanUtils.copyProperties(dept, deptVO);
            return deptVO;
        });
    }

    @Override
    public Integer updateStatus(List<Long> ids, StatusEnum status) {
        LambdaUpdateWrapper<Dept> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Dept::getId, ids)
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
