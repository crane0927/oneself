package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.exception.OneselfException;
import com.oneself.mapper.DeptMapper;
import com.oneself.mapper.UserMapper;
import com.oneself.model.dto.DeptDTO;
import com.oneself.model.dto.DeptQueryDTO;
import com.oneself.model.enums.StatusEnum;
import com.oneself.model.pojo.Dept;
import com.oneself.model.pojo.User;
import com.oneself.model.vo.DeptTreeVO;
import com.oneself.model.vo.DeptVO;
import com.oneself.pagination.MyBatisPageWrapper;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import com.oneself.service.DeptService;
import com.oneself.utils.BeanCopyUtils;
import com.oneself.utils.DuplicateCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

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

    private final CacheManager cacheManager;


    /**
     * 新增部门
     *
     * @param dto 部门信息 DTO，包含部门名称、父部门ID、排序序号等
     * @return 新增部门的ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(DeptDTO dto) {
        // 1. dto 转换为部门对象
        Dept dept = Dept.builder().build();
        BeanCopyUtils.copy(dto, dept);
        // 2. 校验部门名称是否重复
        DuplicateCheckUtils.checkDuplicateMultiFields(
                dept,
                Dept::getId,
                deptMapper::selectCount,
                "部门名称已存在",
                DuplicateCheckUtils.FieldCondition.of(Dept::getParentId, Dept::getParentId, DuplicateCheckUtils.ConditionType.EQ),
                DuplicateCheckUtils.FieldCondition.of(Dept::getDeptName, Dept::getDeptName, DuplicateCheckUtils.ConditionType.EQ)
        );

        // 3. 插入数据库
        int insert = deptMapper.insert(dept);

        // 4. 检查插入结果
        if (insert < 1) {
            throw new OneselfException("部门添加失败");
        }
        log.info("部门添加成功, ID: {}", dept.getId());
        return dept.getId();
    }

    /**
     * 根据ID查询部门信息
     *
     * @param id 部门ID
     * @return 部门信息 VO
     */
    @Override
    @Cacheable(value = "sysDept", key = "#id")
    public DeptVO get(String id) {
        Dept dept = deptMapper.selectById(id);
        if (ObjectUtils.isEmpty(dept)) {
            throw new OneselfException("部门不存在");
        }
        DeptVO deptVO = new DeptVO();
        BeanCopyUtils.copy(dept, deptVO);
        return deptVO;
    }

    /**
     * 更新部门信息
     *
     * @param id  部门ID
     * @param dto 部门信息 DTO，包含更新后的部门名称、父部门ID、排序序号等
     * @return 更新是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysDept", key = "#id")
    public boolean update(String id, DeptDTO dto) {
        // 1. 查询当前部门信息
        Dept currentDept = deptMapper.selectById(id);
        if (ObjectUtils.isEmpty(currentDept)) {
            throw new OneselfException("部门不存在");
        }

        // 2. 构建更新后的部门对象
        Dept dept = Dept.builder().id(id).build();
        BeanCopyUtils.copy(dto, dept);

        // 3. 检查是否修改了父部门，如果修改了需要检查是否形成环状数据
        String newParentId = dept.getParentId();
        String oldParentId = currentDept.getParentId();
        
        // 如果父部门ID发生变化，需要检查环状数据
        if (!Objects.equals(newParentId, oldParentId)) {
            // 3.1 不能将父部门设置为自身
            if (id.equals(newParentId)) {
                throw new OneselfException("不能将父部门设置为自身");
            }
            
            // 3.2 如果新的父部门不为空，检查新的父部门是否是当前部门的子部门（包括所有后代）
            if (StringUtils.isNotBlank(newParentId)) {
                // 获取当前部门的所有子部门ID（包括所有后代）
                List<String> allChildDeptIds = getAllChildDeptIds(Collections.singletonList(id));
                // 如果新的父部门ID在当前部门的子部门列表中，说明会形成环状数据
                if (allChildDeptIds.contains(newParentId)) {
                    throw new OneselfException("不能将父部门设置为当前部门的子部门，否则会形成环状数据");
                }
            }
        }

        // 4. 校验部门名称是否重复
        DuplicateCheckUtils.checkDuplicateMultiFields(
                dept,
                Dept::getId,
                deptMapper::selectCount,
                "部门名称已存在",
                DuplicateCheckUtils.FieldCondition.of(Dept::getParentId, Dept::getParentId, DuplicateCheckUtils.ConditionType.EQ),
                DuplicateCheckUtils.FieldCondition.of(Dept::getDeptName, Dept::getDeptName, DuplicateCheckUtils.ConditionType.EQ)
        );
        
        // 5. 更新部门
        return deptMapper.updateById(dept) > 0;
    }

    /**
     * 批量删除部门（逻辑删除）
     *
     * @param ids 部门ID列表
     * @return 删除是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<String> ids) {
        Assert.noNullElements(ids, "参数不能为空");
        // 1. 获取当前部门 ID 和所有子部门 ID
        List<String> allChildDeptIds = getAllChildDeptIds(ids);
        if (allChildDeptIds.isEmpty()) {
            return true;
        }

        // 2. 删除部门下所有的用户
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().in(User::getDeptId, allChildDeptIds)
        );
        if (!users.isEmpty()) {
            userMapper.deleteByIds(users.stream().map(User::getId).toList());
        }

        // 3. 删除部门
        int deleted = deptMapper.deleteByIds(allChildDeptIds);

        // 4. 事务提交后清理缓存
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Cache deptCache = cacheManager.getCache("sysDept");
                if (deptCache != null) {
                    allChildDeptIds.forEach(deptCache::evict);
                }
            }
        });

        return deleted > 0;
    }

    /**
     * 批量更新部门状态
     *
     * @param ids    部门ID列表
     * @param status 部门状态（启用/禁用）
     * @return 状态更新是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(List<String> ids, StatusEnum status) {
        // 1. 获取传入部门及所有子部门 ID
        List<String> allChildDeptIds = getAllChildDeptIds(ids);

        // 2. 更新用户状态（所有这些部门下的用户）
        userMapper.update(
                User.builder().status(status).build(),
                new LambdaUpdateWrapper<User>().in(User::getDeptId, allChildDeptIds)
        );

        // 3. 更新部门及子部门状态
        LambdaUpdateWrapper<Dept> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Dept::getId, allChildDeptIds)
                .set(Dept::getStatus, status.getCode());
        int updated = deptMapper.update(null, wrapper);

        // 4. 事务提交后清理缓存（避免回滚导致数据不一致）
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Cache deptCache = cacheManager.getCache("sysDept");
                if (deptCache != null) {
                    allChildDeptIds.forEach(deptCache::evict);
                }
            }
        });

        return updated > 0;
    }

    /**
     * 分页查询部门
     *
     * @param dto 分页查询 DTO，包含页码、每页大小及查询条件
     * @return 分页结果 PageVO<DeptVO>
     */
    @Override
    public PageResp<DeptVO> page(PageReq<DeptQueryDTO> dto) {
        // 1. 构建查询条件
        DeptQueryDTO condition = dto.getCondition();
        // 2. 分页参数
        PageReq.Pagination pagination = dto.getPagination();
        // 3. 构建分页参数
        Page<Dept> pageRequest = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        // 4. 查询
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<Dept>()
                .like(StringUtils.isNoneBlank(condition.getDeptName()), Dept::getDeptName, condition.getDeptName()) // 部门名称模糊查询
                .eq(Optional.ofNullable(condition.getStatus()).isPresent(), Dept::getStatus, condition.getStatus()) // 部门状态查询
                .orderByAsc(Dept::getSortOrder); // 排序

        Page<Dept> deptPage = deptMapper.selectPage(pageRequest, wrapper);
        MyBatisPageWrapper<Dept> pageWrapper = new MyBatisPageWrapper<>(deptPage);
        // 5. 转换分页数据
        return PageResp.convert(pageWrapper, dept -> {
            DeptVO deptVO = new DeptVO();
            BeanCopyUtils.copy(dept, deptVO);
            return deptVO;
        });
    }


    /**
     * 递归查询所有子部门的 ID
     *
     * @param ids 父部门 ID 列表
     * @return 所有子部门的 ID 列表
     */
    private List<String> getAllChildDeptIds(List<String> ids) {
        List<String> result = new ArrayList<>(ids);
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
    private void findChildDeptIds(List<String> ids, List<String> result) {
        // 如果传入的父部门 ID 列表为空，直接结束递归
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 查询当前父部门的子部门
        List<String> childIds = deptMapper.selectList(
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

    /**
     * 查询部门树形结构
     * <p>
     * 用于前端展示层级结构的部门树
     * </p>
     *
     * @return 部门树列表
     */
    @Override
    @Cacheable(value = "sysDept", key = "'tree'")
    public List<DeptTreeVO> tree() {
        // 1. 查询全部
        List<Dept> deptList = deptMapper.selectList(null);
        if (ObjectUtils.isEmpty(deptList)) {
            return Collections.emptyList();
        }
        // 2. 转换为 TreeVO
        List<DeptTreeVO> treeVOS = deptList.stream().map(DeptTreeVO::new).toList();

        // 3. 构建 id 与 TreeVO 的映射
        Map<String, DeptTreeVO> idToTreeVOMap = new HashMap<>();
        treeVOS.forEach(vo -> idToTreeVOMap.put(vo.getId(), vo));

        // 4. 构建树结构
        List<DeptTreeVO> rootNodes = new ArrayList<>();
        // 遍历所有 TreeVO，组装树结构
        for (DeptTreeVO vo : treeVOS) {
            if (vo.getParentId() == null) {
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

        return rootNodes;
    }

    /**
     * 查询所有部门列表
     *
     * @return 所有部门信息列表
     */
    @Override
    @Cacheable(value = "sysDept", key = "'all'")
    public List<DeptVO> listAll() {
        List<Dept> depts = deptMapper.selectList(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getStatus, StatusEnum.NORMAL)
                .orderByAsc(Dept::getSortOrder));
        if (CollectionUtils.isEmpty(depts)) {
            return List.of();
        }
        return depts.stream().map(dept -> {
            DeptVO deptVO = new DeptVO();
            BeanCopyUtils.copy(dept, deptVO);
            return deptVO;
        }).toList();
    }

}
