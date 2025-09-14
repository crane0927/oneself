package com.oneself.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.mapper.ConfigurationMapper;
import com.oneself.model.dto.ConfigurationDTO;
import com.oneself.model.dto.ConfigurationQueryDTO;
import com.oneself.model.dto.PageDTO;
import com.oneself.model.enums.ConfigurationTypeEnum;
import com.oneself.model.pojo.Configuration;
import com.oneself.model.vo.ConfigurationVO;
import com.oneself.model.vo.PageVO;
import com.oneself.pagination.MyBatisPageWrapper;
import com.oneself.service.ConfigurationService;
import com.oneself.utils.AssertUtils;
import com.oneself.utils.BeanCopyUtils;
import com.oneself.utils.DuplicateCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.service.impl
 * className ConfigurationServiceImpl
 * description
 * version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationMapper configurationMapper;

    /**
     * 新增系统配置
     *
     * @param dto 配置信息数据传输对象
     * @return 新增配置的主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(ConfigurationDTO dto) {
        Configuration configuration = Configuration.builder().build();
        BeanCopyUtils.copy(dto, configuration);
        DuplicateCheckUtils.checkDuplicateMultiFields(
                configuration,
                Configuration::getId,
                configurationMapper::selectCount,
                "参数 Key 已经存在",
                DuplicateCheckUtils.FieldCondition.of(Configuration::getParamKey, DuplicateCheckUtils.ConditionType.EQ)
        );
        int insert = configurationMapper.insert(configuration);
        AssertUtils.isTrue(insert > 0, "新增参数失败");
        log.info("新增参数成功，参数 ID：{}", configuration.getId());
        return configuration.getId();
    }

    /**
     * 根据 ID 查询系统配置
     *
     * @param id 配置主键 ID
     * @return 配置详情视图对象
     */
    @Override
    public ConfigurationVO get(String id) {
        Configuration configuration = configurationMapper.selectById(id);
        AssertUtils.notNull(configuration, "参数不存在");
        ConfigurationVO vo = new ConfigurationVO();
        BeanCopyUtils.copy(configuration, vo);
        return vo;
    }

    /**
     * 更新系统配置
     *
     * @param id  配置主键 ID
     * @param dto 配置信息数据传输对象
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(String id, ConfigurationDTO dto) {
        Configuration configuration = configurationMapper.selectById(id);
        AssertUtils.notNull(configuration, "参数不存在");
        AssertUtils.isFalse(ConfigurationTypeEnum.SYSTEM.equals(configuration.getType()), "系统内置参数不允许修改");
        BeanCopyUtils.copy(dto, configuration);
        int update = configurationMapper.updateById(configuration);
        AssertUtils.isTrue(update > 0, "更新参数失败");
        return true;
    }

    /**
     * 批量删除系统配置
     *
     * @param ids 配置主键 ID 列表
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<String> ids) {
        ArrayList<String> deleteIds = new ArrayList<>();
        for (String id : ids) {
            Configuration configuration = configurationMapper.selectById(id);
            AssertUtils.notNull(configuration, "参数不存在");
            AssertUtils.isFalse(ConfigurationTypeEnum.SYSTEM.equals(configuration.getType()), "系统内置参数不允许删除");
            deleteIds.add(id);
        }
        int delete = configurationMapper.deleteByIds(deleteIds);
        AssertUtils.isTrue(delete > 0, "删除参数失败");
        return true;
    }

    /**
     * 分页查询系统配置
     *
     * @param dto 分页查询条件对象
     * @return 配置分页结果
     */
    @Override
    public PageVO<ConfigurationVO> page(PageDTO<ConfigurationQueryDTO> dto) {
        // 1. 构建查询条件
        ConfigurationQueryDTO condition = dto.getCondition();
        // 2. 分页参数
        PageDTO.Pagination pagination = dto.getPagination();
        // 3. 构建分页参数
        Page<Configuration> pageRequest = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        // 4. 查询
        LambdaQueryWrapper<Configuration> wrapper = new LambdaQueryWrapper<Configuration>()
                .like(StringUtils.isNoneBlank(condition.getName()), Configuration::getName, condition.getName())
                .eq(StringUtils.isNoneBlank(condition.getParamKey()), Configuration::getParamKey,
                        condition.getParamKey())
                .eq(Optional.ofNullable(condition.getType()).isPresent(), Configuration::getType, condition.getType())
                .orderByAsc(Configuration::getUpdateTime); // 排序

        Page<Configuration> configurationPage = configurationMapper.selectPage(pageRequest, wrapper);
        MyBatisPageWrapper<Configuration> pageWrapper = new MyBatisPageWrapper<>(configurationPage);
        // 5. 转换分页数据
        return PageVO.convert(pageWrapper, configuration -> {
            ConfigurationVO vo = new ConfigurationVO();
            BeanCopyUtils.copy(configuration, vo);
            return vo;
        });
    }
}
