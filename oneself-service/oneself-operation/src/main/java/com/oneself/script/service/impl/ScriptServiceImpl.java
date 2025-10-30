package com.oneself.script.service.impl;

import com.oneself.exception.OneselfException;
import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
import com.oneself.script.model.dto.PageQueryDTO;
import com.oneself.script.model.dto.ScriptDTO;
import com.oneself.script.model.enums.ScriptStatusEnum;
import com.oneself.script.model.mongodb.Script;
import com.oneself.script.model.vo.ScriptVO;
import com.oneself.script.repository.mongodb.ScriptRepository;
import com.oneself.script.service.ScriptService;
import com.oneself.utils.BeanCopyUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author liuhuan
 * date 2025/8/27
 * packageName com.oneself.script.service.impl
 * className ScriptServiceImpl
 * description 脚本业务接口实现
 * version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptServiceImpl implements ScriptService {

    private final ScriptRepository scriptRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public String add(ScriptDTO dto) {
        log.info("新增脚本：{}", dto);

        // 检查名称重复
        boolean exists = scriptRepository.findByStatus(ScriptStatusEnum.ACTIVE)
                .stream()
                .anyMatch(s -> s.getName().equals(dto.getName()));
        if (exists) {
            throw new OneselfException("脚本名已存在，请修改");
        }

        Script script = new Script();

        // 复制字段
        BeanCopyUtils.copy(dto, script);

        // 补充额外字段
        script.setId(UUID.randomUUID().toString());
        script.setVersionNum("v1.0.0");
        script.setLatest(true);
        script.setStatus(ScriptStatusEnum.DRAFT);
        script.setCreateTime(LocalDateTime.now());
        script.setUpdateTime(LocalDateTime.now());

        scriptRepository.save(script);

        return script.getId();
    }

    @Override
    public boolean edit(String id, ScriptDTO dto) {
        Optional<Script> optional = scriptRepository.findById(id);
        if (optional.isEmpty()) {
            throw new OneselfException("脚本不存在");
        }

        Script script = optional.get();
        if (!script.getStatus().equals(ScriptStatusEnum.DRAFT)) {
            throw new OneselfException("脚本状态异常");
        }

        // 检查名称重复（排除当前脚本）
        boolean exists = scriptRepository.findByStatus(ScriptStatusEnum.ACTIVE)
                .stream()
                .anyMatch(s -> !s.getId().equals(id) && s.getName().equals(dto.getName()));
        if (exists) {
            throw new OneselfException("脚本名已存在，请修改");
        }

        // 复制 DTO 字段到实体
        BeanCopyUtils.copy(dto, script);

        // 更新时间
        script.setUpdateTime(LocalDateTime.now());
        scriptRepository.save(script);

        return true;
    }

    @Override
    public PageResp<ScriptVO> page(PageReq<PageQueryDTO> dto) {
        PageQueryDTO queryDTO = dto.getCondition();
        PageReq.Pagination pagination = dto.getPagination();

        int page = pagination.getPageNum().intValue() - 1;
        int size = pagination.getPageSize().intValue();

        // 构建查询条件
        Criteria criteria = new Criteria();
        if (queryDTO != null) {
            if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
                criteria.and("name").regex(".*" + queryDTO.getName() + ".*", "i"); // 模糊查询
            }
            if (queryDTO.getStatus() != null) {
                criteria.and("status").is(queryDTO.getStatus());
            }
            if (queryDTO.getTags() != null && !queryDTO.getTags().isEmpty()) {
                criteria.and("tags").in(queryDTO.getTags());
            }
        }

        Query query = new Query(criteria);
        long total = mongoTemplate.count(query, Script.class);

        // 排序
        if (pagination.getSorts() != null && !pagination.getSorts().isEmpty()) {
            List<org.springframework.data.domain.Sort.Order> orders = new ArrayList<>();
            for (PageReq.Sort sort : pagination.getSorts()) {
                // 获取字段名（如果为空，可根据业务决定是否跳过或抛异常）
                String field = sort.getField();
                if (field == null || field.trim().isEmpty()) {
                    // 处理空字段的情况（示例：跳过该排序条件）
                    continue;
                }
                // 转换排序方向（默认降序）
                org.springframework.data.domain.Sort.Direction direction =
                        sort.getDirection() == PageReq.SortDirection.ASC
                                ? org.springframework.data.domain.Sort.Direction.ASC
                                : org.springframework.data.domain.Sort.Direction.DESC;
                orders.add(new org.springframework.data.domain.Sort.Order(direction, field));
            }
            // 确保有有效排序条件时才应用
            if (!orders.isEmpty()) {
                query.with(org.springframework.data.domain.Sort.by(orders));
            } else {
                // 若排序列表不为空但所有条件无效，使用默认排序
                query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "updateTime"));
            }
        } else {
            // 无排序条件时使用默认排序
            query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "updateTime"));
        }

        // 分页
        query.skip((long) page * size).limit(size);

        List<Script> list = mongoTemplate.find(query, Script.class);

        // 转换为 VO
        List<ScriptVO> records = list.stream().map(ScriptVO::new).toList();
        long pages = (total + size - 1) / size;

        return PageResp.success(records, total, (long) size, pages);
    }

    @Override
    public ScriptVO get(String id) {
        // 根据 ID 查询脚本
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new OneselfException("脚本不存在"));

        // 转换为 VO 返回
        return new ScriptVO(script);
    }

    @Override
    public boolean delete(List<@NotNull String> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 逻辑删除：更新 deleted 字段
        List<Script> scripts = scriptRepository.findAllById(ids);
        if (scripts.isEmpty()) {
            return false;
        }

        scripts.forEach(script -> script.setDeleted(true));
        scriptRepository.saveAll(scripts);
        return true;
    }
}
