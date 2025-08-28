package com.oneself.script.service.impl;

import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.script.model.dto.PageQueryDTO;
import com.oneself.script.model.dto.ScriptDTO;
import com.oneself.script.model.vo.ScriptVO;
import com.oneself.script.service.ScriptService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Override
    public Long add(ScriptDTO dto) {
        return 0L;
    }

    @Override
    public boolean edit(Long id, ScriptDTO dto) {
        return false;
    }

    @Override
    public PageVO<ScriptVO> page(PageDTO<PageQueryDTO> dto) {
        return null;
    }

    @Override
    public ScriptVO get(Long id) {
        return null;
    }

    @Override
    public boolean delete(List<@NotNull Long> ids) {
        return false;
    }
}
