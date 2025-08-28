package com.oneself.script.service;

import com.oneself.model.dto.PageDTO;
import com.oneself.model.vo.PageVO;
import com.oneself.script.model.dto.PageQueryDTO;
import com.oneself.script.model.dto.ScriptDTO;
import com.oneself.script.model.vo.ScriptVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/8/27
 * packageName com.oneself.script.service
 * interfaceName ScriptService
 * description 脚本业务接口
 * version 1.0
 */
public interface ScriptService {
    Long add(@Valid ScriptDTO dto);

    boolean edit(@Valid @NotNull @Positive Long id, @Valid ScriptDTO dto);

    PageVO<ScriptVO> page(@Valid PageDTO<PageQueryDTO> dto);

    ScriptVO get(@Valid @NotNull @Positive Long id);

    boolean delete(@Valid @NotEmpty List<@NotNull Long> ids);
}
