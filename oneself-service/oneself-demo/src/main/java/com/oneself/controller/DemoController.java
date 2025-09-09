package com.oneself.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import com.oneself.annotation.ApiLog;
import com.oneself.annotation.RequireLogin;
import com.oneself.model.dto.DemoDTO;
import com.oneself.model.vo.DemoVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.service.OneselfService;
import com.oneself.utils.ElasticsearchUtils;
import com.oneself.utils.ExportFileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
@RequiredArgsConstructor
@RequireLogin
@ApiLog
@RestController
@RefreshScope
@RequestMapping({"/demo"})
public class DemoController {

    @Value("${name:123}")
    private String name;

    private final ElasticsearchUtils elasticsearchUtils;

    private final OneselfService oneselfService;

    @ApiLog
    @Operation(summary = "你好 xxx")
    @PostMapping("/hello")
    public ResponseVO<DemoVO> sayHello(@RequestBody @Valid DemoDTO dto) {
        DemoVO demoVO = new DemoVO();
        demoVO.setInfo("hello " + dto.getName());
        return ResponseVO.success(demoVO);
    }

    @Operation(summary = "Elasticsearch 集群名称获取")
    @GetMapping("/elasticsearch/cluster")
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
    @GetMapping("/properties")
    public ResponseVO<String> getProperties() {
        return ResponseVO.success(name);
    }

    @Operation(summary = "自定义 Starter 功能测试")
    @GetMapping("/starter")
    public ResponseVO<String> getStarterInfo() {
        return ResponseVO.success(oneselfService.doSomething());
    }

    @ApiLog(logRequest = false, logResponse = false)
    @Operation(summary = "导出文件")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        List<Map<String, Object>> list = generateTestData();

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("name", "姓名");
        headers.put("age", "年龄");
        headers.put("sex", "性别");

        try {
            // 如果 ExportFileUtils 没设置响应头，这里添加
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("test.xls", "UTF-8"));

            ExportFileUtils.exportToExcel(response, "test.xls", list, headers);
        } catch (Exception e) {
            log.error("导出文件异常: {}", e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> generateTestData() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", "张三" + i);
            map.put("age", 18 + i);
            map.put("sex", "男");
            list.add(map);
        }
        return list;
    }
}
