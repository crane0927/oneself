package com.oneself.script.service;

import com.oneself.req.PageReq;
import com.oneself.resp.PageResp;
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
    String add(@Valid ScriptDTO dto);

    boolean edit(@Valid @NotNull @Positive String id, @Valid ScriptDTO dto);

    PageResp<ScriptVO> page(@Valid PageReq<PageQueryDTO> dto);

    ScriptVO get(@Valid @NotNull @Positive String id);

    boolean delete(@Valid @NotEmpty List<@NotNull String> ids);
}
