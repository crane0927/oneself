package com.oneself.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.DemoDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.utils.ElasticsearchUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    private final ElasticsearchUtils elasticsearchUtils;

    @Autowired
    public DemoController(ElasticsearchUtils elasticsearchUtils) {
        this.elasticsearchUtils = elasticsearchUtils;
    }

    @ApiOperation(value = "你好 xxx")
    @PostMapping({"/say/hello"})
    public ResponseVO<DemoVO> sayHello(@RequestBody DemoDTO dto) {
        log.info("hello {}", dto.getName());
        DemoVO demoVO = new DemoVO("hello " + dto.getName());
        return ResponseVO.success(demoVO);
    }

    @ApiOperation(value = "Elasticsearch 集群名称获取")
    @GetMapping({"/get/elasticsearch/cluster/info"})
    public ResponseVO<DemoVO> getElasticsearchClusterInfo() {
        ElasticsearchClient client = elasticsearchUtils.getClient();
        try {
            InfoResponse info = client.info();
            log.info("Elasticsearch 集群名称: {}", info.clusterName());
            DemoVO demoVO = new DemoVO(info.clusterName());
            return ResponseVO.success(demoVO);
        } catch (Exception e) {
            log.error("Elasticsearch 集群名称获取异常: {}", e.getMessage());
            return ResponseVO.failure("Elasticsearch 集群名称获取异常: " + e.getMessage());
        }

    }
}
