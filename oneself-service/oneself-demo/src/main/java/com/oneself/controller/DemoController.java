package com.oneself.controller;

import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.DemoDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.controller
 * className DemoController
 * description Demo
 * version 1.0
 */
@Api(tags = "接口样例")
@Slf4j
@RequireLogin
@RestController
@RequestMapping({"/demo"})
public class DemoController {

    @ApiOperation(value = "你好 xxx")
    @PostMapping({"/say/hello"})
    public ResponseVO<DemoVO> sayHello(@RequestBody DemoDTO dto) {
        log.info("hello {}", dto.getName());
        DemoVO demoVO = new DemoVO("hello " + dto.getName());
        return ResponseVO.success(demoVO);
    }
}
