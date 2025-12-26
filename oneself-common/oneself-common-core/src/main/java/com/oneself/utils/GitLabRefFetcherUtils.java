package com.oneself.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.oneself.model.bo.GitLabUrlParseResultBO;
import com.oneself.model.bo.GitRefInfoBO;
import com.oneself.model.enums.BranchTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.utils
 * className GitLabRefFetcherUtils
 * description  GitLab 引用信息获取工具类
 * version 1.0
 */
@Slf4j
public class GitLabRefFetcherUtils {

    /**
     * 获取 GitLab 项目的分支或标签列表，并按最近提交时间降序排序（活跃度高的排前面）。
     *
     * @param gitlabUrl    GitLab 服务地址
     * @param projectPath  项目路径（命名空间/项目名）
     * @param privateToken GitLab 的私有访问令牌（需具备 read_repository 权限）
     * @param type         分支类型枚举：BRANCH 或 TAG
     * @return 分支或标签列表，每项包含名称和最后一次提交时间
     * @throws IllegalArgumentException 参数不合法时抛出
     * @throws RuntimeException         请求或解析过程中出现异常时抛出
     */
    public static List<GitRefInfoBO> fetchRefs(
            String gitlabUrl,
            String projectPath,
            String privateToken,
            BranchTypeEnum type
    ) {
        // 参数验证
        validateParameters(gitlabUrl, projectPath, privateToken, type);

        try {
            String encodedPath = URLEncoder.encode(projectPath, StandardCharsets.UTF_8);
            String endpoint = buildEndpoint(gitlabUrl, encodedPath, type);

            String response = executeGitLabRequest(endpoint, privateToken);
            return parseAndSortGitRefs(response);

        } catch (IOException e) {
            throw new RuntimeException("请求 GitLab 接口或解析响应失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("处理 GitLab 项目引用信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重载方法，从完整URL解析并获取GitLab引用信息
     */
    public static List<GitRefInfoBO> fetchRefs(
            String url,
            String privateToken,
            BranchTypeEnum type
    ) {
        Objects.requireNonNull(url, "GitLab 项目 URL 不能为空");
        Objects.requireNonNull(privateToken, "私有访问令牌不能为空");
        Objects.requireNonNull(type, "分支类型不能为空");

        GitLabUrlParseResultBO parsedInfo = parseGitLabUrl(url);
        String gitlabUrl = parsedInfo.getGitlabUrl();
        String projectPath = parsedInfo.getProjectPath();

        log.info("已解析的 GitLab URL: {},项目路径: {}", gitlabUrl, projectPath);
        return fetchRefs(gitlabUrl, projectPath, privateToken, type);
    }

    /**
     * 参数验证
     */
    private static void validateParameters(String gitlabUrl, String projectPath,
                                           String privateToken, BranchTypeEnum type) {
        Objects.requireNonNull(gitlabUrl, "GitLab 服务地址不能为空");
        Objects.requireNonNull(projectPath, "项目路径不能为空");
        Objects.requireNonNull(privateToken, "私有访问令牌不能为空");
        Objects.requireNonNull(type, "分支类型不能为空");

        if (!BranchTypeEnum.BRANCH.equals(type) && !BranchTypeEnum.TAG.equals(type)) {
            throw new IllegalArgumentException("不支持的类型: " + type + " (必须是 BRANCH 或 TAG)");
        }
    }

    /**
     * 构建API端点URL
     */
    private static String buildEndpoint(String gitlabUrl, String encodedPath, BranchTypeEnum type) {
        String endpointPath = BranchTypeEnum.BRANCH.equals(type)
                ? "repository/branches"
                : "repository/tags";

        // 确保gitlabUrl不以斜杠结尾
        String normalizedGitlabUrl = gitlabUrl.replaceAll("/$", "");
        return String.format("%s/api/v4/projects/%s/%s", normalizedGitlabUrl, encodedPath, endpointPath);
    }

    /**
     * 执行GitLab API请求
     */
    private static String executeGitLabRequest(String endpoint, String privateToken) throws IOException {
        log.debug("请求GitLab API: {}", endpoint);

        return Request.Get(endpoint)
                .addHeader("PRIVATE-TOKEN", privateToken)
                .addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType())
                .execute()
                .returnContent()
                .asString();
    }

    /**
     * 解析响应并排序引用信息
     */
    private static List<GitRefInfoBO> parseAndSortGitRefs(String response) throws IOException {
        JsonNode jsonArray = JacksonUtils.getObjectMapper().readTree(response);
        List<GitRefInfoBO> result = new ArrayList<>();

        if (jsonArray == null || !jsonArray.isArray()) {
            log.warn("GitLab 返回的不是有效的数组响应");
            return result;
        }

        for (JsonNode node : jsonArray) {
            if (node == null) continue;

            String name = node.get("name") != null ? node.get("name").asText() : null;
            JsonNode commitNode = node.path("commit");
            String lastCommit = commitNode.path("committed_date").asText();
            String shortId = commitNode.path("short_id").asText();

            if (name != null && !name.isEmpty()) {
                result.add(new GitRefInfoBO(name, lastCommit, shortId));
            } else {
                log.warn("跳过无效的引用信息: {}", node);
            }
        }

        // 按提交时间降序排序
        result.sort(Comparator.comparing(GitRefInfoBO::getLastCommit).reversed());
        return result;
    }


    /**
     * 从 GitLab 项目 URL 中提取 GitLab 根地址和项目路径（命名空间/项目名）
     *
     * @param projectUrl 完整的 GitLab 项目 URL
     * @return 解析结果BO，包含gitlabUrl和projectPath
     * @throws IllegalArgumentException 当URL格式非法时抛出
     */
    public static GitLabUrlParseResultBO parseGitLabUrl(String projectUrl) {
        Objects.requireNonNull(projectUrl, "项目 URL 不能为空");

        try {
            String urlStr = preprocessUrl(projectUrl.trim());

            // 使用URI和URL的组合避免URL(String)构造方法的弃用问题
            URI uri = new URI(urlStr);
            URL url = uri.toURL();

            String gitlabUrl = buildGitLabBaseUrl(url);
            String projectPath = extractProjectPath(url);

            return new GitLabUrlParseResultBO(gitlabUrl, projectPath);
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException("非法的 GitLab 项目 URL 格式: " + projectUrl, e);
        } catch (Exception e) {
            throw new IllegalArgumentException("解析 GitLab 项目 URL 失败: " + projectUrl, e);
        }
    }


    /**
     * 预处理URL，处理不同格式的URL
     */
    private static String preprocessUrl(String urlStr) {
        // 处理.git后缀
        if (urlStr.endsWith(".git")) {
            urlStr = urlStr.substring(0, urlStr.length() - 4);
        }

        // 处理SSH格式URL (git@xxx:xxx/xxx)
        if (urlStr.startsWith("git@")) {
            urlStr = urlStr.replaceFirst(":", "/")
                    .replaceFirst("git@", "http://");
        }

        // 处理可能包含的用户名密码
        urlStr = urlStr.replaceAll("(http[s]?://)[^@]+@", "$1");

        return urlStr;
    }

    /**
     * 构建GitLab基础URL
     */
    private static String buildGitLabBaseUrl(URL url) {
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(url.getProtocol()).append("://").append(url.getHost());

        if (url.getPort() != -1) {
            baseUrl.append(":").append(url.getPort());
        }

        return baseUrl.toString();
    }

    /**
     * 提取项目路径
     */
    private static String extractProjectPath(URL url) {
        String path = url.getPath();
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("URL 中未包含项目路径信息");
        }

        // 去除开头的斜杠
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }
}