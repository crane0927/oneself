package com.oneself.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.DemoDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.utils.ElasticsearchUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.controller
 * className DemoController
 * description Demo
 * version 1.0
 */
@Tag(name = "接口样例")
@Slf4j
@RequireLogin
@RequestLogging
@RestController
@RequestMapping({"/demo"})
public class DemoController {

    @Value("${oneself.name:123}")
    private String name;

    private final ElasticsearchUtils elasticsearchUtils;

    @Autowired
    public DemoController(ElasticsearchUtils elasticsearchUtils) {
        this.elasticsearchUtils = elasticsearchUtils;
    }

    @Operation(summary = "你好 xxx")
    @PostMapping({"/say/hello"})
    public ResponseVO<DemoVO> sayHello(@RequestBody @Valid DemoDTO dto) {
        DemoVO demoVO = new DemoVO();
        demoVO.setInfo("hello " + dto.getName());
        return ResponseVO.success(demoVO);
    }

    @Operation(summary = "Elasticsearch 集群名称获取")
    @GetMapping({"/get/elasticsearch/cluster/info"})
    public ResponseVO<DemoVO> getElasticsearchClusterInfo() {
        ElasticsearchClient client = elasticsearchUtils.getClient();
        try {
            InfoResponse info = client.info();
            log.info("Elasticsearch 集群名称: {}", info.clusterName());
            DemoVO demoVO = new DemoVO();
            demoVO.setInfo(info.clusterName());
            return ResponseVO.success(demoVO);
        } catch (Exception e) {
            log.error("Elasticsearch 集群名称获取异常: {}", e.getMessage());
            return ResponseVO.failure("Elasticsearch 集群名称获取异常: " + e.getMessage());
        }

    }

    @Operation(summary = "配置文件信息读取")
    @GetMapping({"/get/properties"})
    public ResponseVO<String > getProperties() {
        return ResponseVO.success(name);
    }
}
