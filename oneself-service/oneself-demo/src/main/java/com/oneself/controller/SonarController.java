package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.vo.LanguageVO;
import com.oneself.resp.Resp;
import com.oneself.utils.JacksonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.sonarqube.ws.Issues;
import org.sonarqube.ws.Projects;
import org.sonarqube.ws.Rules;
import org.sonarqube.ws.UserTokens;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.issues.IssuesService;
import org.sonarqube.ws.client.languages.LanguagesService;
import org.sonarqube.ws.client.languages.ListRequest;
import org.sonarqube.ws.client.projects.ProjectsService;
import org.sonarqube.ws.client.projects.SearchRequest;
import org.sonarqube.ws.client.rules.RulesService;
import org.sonarqube.ws.client.usertokens.GenerateRequest;
import org.sonarqube.ws.client.usertokens.UserTokensService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/2/17
 * packageName com.oneself.controller
 * className SonarController
 * description
 * version 1.0
 */
@Slf4j
@Tag(name = "Sonar 测试")
@RestController
@RequestMapping({"/sonar"})
public class SonarController {

    private static final WsClient wsClient;

    static {
        HttpConnector httpConnector = HttpConnector
                .newBuilder()
                .url("http://localhost:9000")
                .token("squ_7d6aa3dda5bd4df837cca2c51ab3ae46d9e4e0d8")
                .build();
        // 创建 SonarQube Web 服务客户端
        wsClient = WsClientFactories.getDefault().newClient(httpConnector);
    }

    @ApiLog(logRequest = false, logResponse = false)
    @Operation(summary = "获取项目列表")
    @GetMapping("/projects")
    public Resp<List<String>> getProjects() {
        // 获取 ProjectsService，用于操作 SonarQube 项目数据
        ProjectsService projects = wsClient.projects();

        // 创建搜索请求（根据需要可以自定义）
        SearchRequest searchRequest = new SearchRequest();

        // 调用 API 获取项目组件列表
        Projects.SearchWsResponse search = projects.search(searchRequest);

        // 提取组件列表，并映射为组件名称的列表
        List<Projects.SearchWsResponse.Component> componentsList = search.getComponentsList();
        List<String> list = componentsList
                .stream()
                .map(Projects.SearchWsResponse.Component::getName)
                .toList();  // 将名称收集成 List<String>

        return Resp.success(list);
    }


    @ApiLog(logRequest = false, logResponse = false)
    @Operation(summary = "获取支持的语言")
    @GetMapping("/languages")
    public Resp<List<LanguageVO.Language>> getLanguages() {
        LanguagesService languages = wsClient.languages();

        ListRequest listRequest = new ListRequest();
        String languagesStr = languages.list(listRequest);

        LanguageVO languageVO = JacksonUtils.fromJson(languagesStr, LanguageVO.class);
        if (ObjectUtils.isNotEmpty(languageVO)) {
            log.info("语言种类数量: {}", languageVO.getLanguages().size());
            return Resp.success(languageVO.getLanguages());
        }
        return Resp.failure(null);
    }

    @ApiLog(logRequest = false, logResponse = false)
    @Operation(summary = "获取对应语言的规则")
    @GetMapping("/rules")
    public Resp<List<String>> getRules() {
        RulesService rules = wsClient.rules();
        org.sonarqube.ws.client.rules.SearchRequest searchRequest = new org.sonarqube.ws.client.rules.SearchRequest();
        // 设置查询条件，这里只查询 Java 语言的规则，通过 key 查询
        searchRequest.setLanguages(Collections.singletonList("java"));
        // 默认只返回 100 条数据，可以通过设置 p 和 ps 参数来分页查询
        searchRequest.setPs("500");
        searchRequest.setP("1");

        Rules.SearchResponse search = rules.search(searchRequest);
        List<Rules.Rule> rulesList = search.getRulesList();

        List<String> list = rulesList.stream().map(Rules.Rule::getKey).toList();
        log.info("规则数量: {}", list.size());
        return Resp.success(list);
    }


    @ApiLog(logRequest = false, logResponse = false)
    @Operation(summary = "获取项目的所有问题列表")
    @GetMapping("/issues")
    public void getProjectIssues() {
        IssuesService issuesService = wsClient.issues();

        // 创建查询请求对象
        org.sonarqube.ws.client.issues.SearchRequest searchRequest = new org.sonarqube.ws.client.issues.SearchRequest();
        searchRequest.setProjects(Collections.singletonList("oneself-demo"));  // 设置项目的key
        searchRequest.setPs("500");  // 每页返回 500 条问题
        searchRequest.setP("1");
        Issues.SearchWsResponse search = issuesService.search(searchRequest);
        log.info("问题数量: {}", search.getIssuesCount());
        search.getIssuesList().forEach(issue -> {
            log.info("问题key: {}", issue.getKey());
            log.info("问题类型: {}", issue.getType());
            log.info("问题规则key: {}", issue.getRule());
            log.info("问题代码路径: {}", issue.getComponent());
            log.info("问题信息: {}", issue.getMessage());

            issue.getFlowsList().forEach(flow -> {
                log.info("产生问题的原因: {}", flow.getDescription());
                log.info("堆栈信息: {}", flow.getInitializationErrorString());
            });
        });


    }

    @ApiLog(logRequest = false, logResponse = false)
    @Operation(summary = "根据规则 key 获取规则的详细信息")
    @GetMapping("/rules/{key}")
    public void getRuleDetails(@PathVariable String key) {
        // 获取规则服务
        RulesService rulesService = wsClient.rules();
        String ruleKey = key;  // 规则 key

        // 创建查询请求对象
        org.sonarqube.ws.client.rules.ShowRequest showRequest = new org.sonarqube.ws.client.rules.ShowRequest();
        showRequest.setKey(ruleKey);  // 设置规则的 key

        // 查询规则的详细信息
        Rules.ShowResponse showResponse = rulesService.show(showRequest);

        // 输出规则的详细信息
        log.info("规则 key: {}", showResponse.getRule().getKey());
        log.info("规则名称: {}", showResponse.getRule().getName());
        log.info("规则描述: {}", showResponse.getRule().getMdDesc());
        log.info("规则描述: {}", showResponse.getRule().getParams());
        log.info("规则语言: {}", showResponse.getRule().getLangName());
        log.info("规则严重性: {}", showResponse.getRule().getSeverity());
        log.info("规则类型: {}", showResponse.getRule().getType());
    }

    @ApiLog(logRequest = false, logResponse = false)
    @Operation(summary = "生成用户 Token")
    @PostMapping("/tokens")
    public void getUserToken() {
        UserTokensService tokensService = wsClient.userTokens();
        GenerateRequest generateRequest = new GenerateRequest();
        generateRequest.setLogin("admin"); //当前登录用户
        generateRequest.setName("oneself-demo"); // token 名称
        generateRequest.setType("USER_TOKEN"); // 用户令牌：USER_TOKEN、  全局分析令牌：GLOBAL_ANALYSIS_TOKEN、项目分析令牌：PROJECT_ANALYSIS_TOKEN
//        generateRequest.setProjectKey("oneself-demo"); // 项目名称 只有项目分析令牌需要
        generateRequest.setExpirationDate("2025-03-19"); // 过期时间 永不过期则不传
        UserTokens.GenerateWsResponse generate = tokensService.generate(generateRequest);
        String token = generate.getToken();
        log.info("token: {}", token);
    }

}
