package com.oneself.dept.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.common.model.enums.StatusEnum;
import com.oneself.dept.mapper.DeptMapper;
import com.oneself.dept.model.dto.DeptDTO;
import com.oneself.dept.model.dto.PageDeptDTO;
import com.oneself.dept.model.pojo.Dept;
import com.oneself.dept.model.vo.DeptTreeVO;
import com.oneself.dept.model.vo.DeptVO;
import com.oneself.dept.service.DeptService;
import com.oneself.exception.OneselfException;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.user.mapper.UserMapper;
import com.oneself.user.model.pojo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.service.impl
 * className DeptServiceImpl
 * description 部门接口实现类
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;
    private final UserMapper userMapper;

    @Override
    public Integer addDept(DeptDTO dto) {
        // 1. 构建部门对象
        Dept dept = Dept.builder().build();
        BeanUtils.copyProperties(dto, dept);
        Long parentId = dept.getParentId();
        if (parentId != 0) {
            // 2. 校验上级部门是否存在
            Dept parentDept = deptMapper.selectById(parentId);
            if (ObjectUtils.isEmpty(parentDept)) {
                throw new OneselfException("上级部门不存在");
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
            throw new OneselfException("部门不存在");
        }
        DeptVO deptVO = new DeptVO();
        BeanUtils.copyProperties(dept, deptVO);
        return deptVO;
    }

    @Override
    @Transactional
    public Integer updateDept(Long id, DeptDTO dto) {
        // 1. 构建部门对象
        Dept dept = Dept.builder().id(id).build();
        BeanUtils.copyProperties(dto, dept);
        // 2. 校验部门名称是否重复
        checkDeptName(dept);
        Long parentId = dept.getParentId();
        if (parentId != 0) {
            // 3. 校验上级部门是否存在
            Dept parentDept = deptMapper.selectById(parentId);
            if (ObjectUtils.isEmpty(parentDept)) {
                throw new OneselfException("上级部门不存在");
            }
        }
        // 4. 更新部门
        return deptMapper.updateById(dept);
    }

    @Override
    @Transactional
    public Integer deleteDept(List<Long> ids) {
        // 1. 获取当前部门 ID 和所有子部门 ID
        List<Long> allChildDeptIds = getAllChildDeptIds(ids);
        if (CollectionUtils.isEmpty(allChildDeptIds)) {
            throw new OneselfException("删除失败，该部门不存在");
        }
        // 2. 删除部门下所有的用户
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getDeptId, allChildDeptIds));
        userMapper.deleteByIds(users);
        // 3. 删除部门
        return deptMapper.deleteByIds(allChildDeptIds);
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
    @Transactional
    public Integer updateStatus(List<Long> ids, StatusEnum status) {
        // 1. 获取当前部门 ID 和所有子部门 ID
        List<Long> allChildDeptIds = getAllChildDeptIds(ids);

        // 2. 更新用户状态
        userMapper.update(User.builder().status(status).build(),
                new LambdaUpdateWrapper<User>().in(User::getDeptId, allChildDeptIds));

        // 3. 更新部门及子部门状态
        LambdaUpdateWrapper<Dept> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Dept::getId, allChildDeptIds)
                .set(Dept::getStatus, status.getCode());
        return deptMapper.update(null, wrapper);
    }

    /**
     * 递归查询所有子部门的 ID
     *
     * @param ids 父部门 ID 列表
     * @return 所有子部门的 ID 列表
     */
    private List<Long> getAllChildDeptIds(List<Long> ids) {
        List<Long> result = new ArrayList<>(ids);
        // 递归查询子部门
        findChildDeptIds(ids, result);
        return result;
    }

    /**
     * 辅助递归方法
     *
     * @param ids    父部门 ID 列表
     * @param result 保存结果的列表
     */
    private void findChildDeptIds(List<Long> ids, List<Long> result) {
        // 如果传入的父部门 ID 列表为空，直接结束递归
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 查询当前父部门的子部门
        List<Long> childIds = deptMapper.selectList(
                        new LambdaQueryWrapper<Dept>().in(Dept::getParentId, ids))
                .stream()
                .map(Dept::getId)
                .toList();

        // 如果没有子部门，结束递归
        if (childIds.isEmpty()) {
            return;
        }

        // 将当前层级的子部门 ID 添加到结果中
        result.addAll(childIds);

        // 递归查询每个子部门的子部门
        findChildDeptIds(childIds, result);
    }

    @Override
    public ResponseVO<List<DeptTreeVO>> getTree() {
        // 1. 查询全部
        List<Dept> deptList = deptMapper.selectList(null);
        if (ObjectUtils.isEmpty(deptList)) {
            return ResponseVO.success(Collections.emptyList());
        }
        // 2. 转换为 TreeVO
        List<DeptTreeVO> treeVOS = deptList.stream().map(DeptTreeVO::new).toList();

        // 3. 构建 id 与 TreeVO 的映射
        Map<Long, DeptTreeVO> idToTreeVOMap = new HashMap<>();
        treeVOS.forEach(vo -> idToTreeVOMap.put(vo.getId(), vo));

        // 4. 构建树结构
        List<DeptTreeVO> rootNodes = new ArrayList<>();
        // 遍历所有 TreeVO，组装树结构
        for (DeptTreeVO vo : treeVOS) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                // 没有父节点的为顶级节点
                rootNodes.add(vo);
            } else {
                // 找到父节点，并把当前节点加入父节点的 children 中
                DeptTreeVO parent = idToTreeVOMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }

        return ResponseVO.success(rootNodes);
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
            throw new OneselfException("部门名称已存在");
        }

    }
}
