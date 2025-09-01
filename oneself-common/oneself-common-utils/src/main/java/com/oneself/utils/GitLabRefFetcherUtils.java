package com.oneself.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.oneself.model.bo.GitRefInfoBO;
import com.oneself.model.enums.BranchTypeEnum;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.utils
 * className GitLabRefFetcherUtils
 * description  GitLab 引用信息获取工具类
 * version 1.0
 */
public class GitLabRefFetcherUtils {


    /**
     * 获取 GitLab 项目的分支或标签列表，并按最近提交时间降序排序（活跃度高的排前面）。
     *
     * @param gitlabUrl    GitLab 服务地址，例如：http://192.168.199.244
     * @param projectPath  项目路径（命名空间/项目名），例如：shsnc-domp/snc-datacollection
     * @param privateToken GitLab 的私有访问令牌（需具备 read_repository 权限）
     * @param type         分支类型枚举：BRANCH 或 TAG
     * @return 分支或标签列表，每项包含名称和最后一次提交时间
     * @throws Exception 请求或解析过程中出现的异常
     */
    public static List<GitRefInfoBO> fetchRefs(
            String gitlabUrl,
            String projectPath,
            String privateToken,
            BranchTypeEnum type
    ) {
        try {
            String encodedPath = URLEncoder.encode(projectPath, StandardCharsets.UTF_8.toString());
            String endpoint;

            if (BranchTypeEnum.BRANCH.equals(type)) {
                endpoint = String.format("%s/api/v4/projects/%s/repository/branches", gitlabUrl, encodedPath);
            } else if (BranchTypeEnum.TAG.equals(type)) {
                endpoint = String.format("%s/api/v4/projects/%s/repository/tags", gitlabUrl, encodedPath);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + type + " (must be BRANCH or TAG)");
            }

            String response = Request.Get(endpoint)
                    .addHeader("PRIVATE-TOKEN", privateToken)
                    .execute()
                    .returnContent()
                    .asString();

            JsonNode jsonArray = JacksonUtils.getObjectMapper().readTree(response);
            List<GitRefInfoBO> result = new ArrayList<>();

            for (JsonNode node : jsonArray) {
                String name = node.get("name").asText();
                String lastCommit = node.path("commit").path("committed_date").asText();
                String shortId = node.path("commit").path("short_id").asText();

                result.add(new GitRefInfoBO(name, lastCommit, shortId));
            }

            // 按提交时间降序排序
            result.sort(Comparator.comparing(GitRefInfoBO::getLastCommit).reversed());
            return result;

        } catch (IOException e) {
            throw new RuntimeException("请求 GitLab 接口或解析响应失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("处理 GitLab 项目引用信息失败: " + e.getMessage(), e);
        }
    }

    public static List<GitRefInfoBO> fetchRefs(
            String url,
            String privateToken,
            BranchTypeEnum type
    ) {
        Map<String, String> map = parseGitLabUrl(url);
        return fetchRefs(map.get("gitlabUrl"), map.get("projectPath"), privateToken, type);
    }

    /**
     * 从 GitLab 项目 URL 中提取 GitLab 根地址和项目路径（命名空间/项目名）
     *
     * @param projectUrl 完整的 GitLab 项目 URL，例如：http://192.168.199.244/shsnc-domp/snc-audit
     * @return Map，包含：
     * - "gitlabUrl": 根地址，例如 http://192.168.199.244
     * - "projectPath": 项目路径，例如 shsnc-domp/snc-audit
     */
    private static Map<String, String> parseGitLabUrl(String projectUrl) {
        Map<String, String> result = new HashMap<>();
        try {
            URL url = new URL(projectUrl);
            String gitlabUrl = url.getProtocol() + "://" + url.getHost();
            if (url.getPort() != -1) {
                gitlabUrl += ":" + url.getPort();
            }

            String path = url.getPath(); // /shsnc-domp/snc-audit
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            result.put("gitlabUrl", gitlabUrl);
            result.put("projectPath", path);
        } catch (Exception e) {
            throw new IllegalArgumentException("非法 GitLab 项目地址: " + projectUrl, e);
        }

        return result;
    }

    /**
     * 获取 GitLab 项目地址
     *
     * @param projectUrl 项目地址 git 或 http 格式
     * @return GitLab 项目地址
     */
    public static String getGitLabUrl(String projectUrl) {
        String url = projectUrl.trim();
        boolean endsWithGit = url.endsWith(".git");
        boolean startsWithGit = url.startsWith("git@");
        // 去掉 .git 后缀
        if (endsWithGit) {
            url = url.substring(0, url.length() - 4);
        }

        // git@ 转换为 http://
        if (startsWithGit) {
            // git@host:user/repo → http://host/user/repo
            url = url.replaceFirst(":", "/")
                    .replaceFirst("git@", "http://");
        }
        url = url.replaceAll("(http://)[^@]+@", "$1");
        return url;
    }

    // 示例调用方式
    public static void main(String[] args) throws Exception {
        String gitlabUrl = "http://wx-chism3@10.38.124.5:8080/107BaseComponent/chntitom.git";

        String url = getGitLabUrl(gitlabUrl);

        String token = "7dJLgTAKSgMZcV6xXw6a";


        List<GitRefInfoBO> branches = fetchRefs(url, token, BranchTypeEnum.BRANCH);
        List<GitRefInfoBO> tags = fetchRefs(url, token, BranchTypeEnum.TAG);

        System.out.println("Branches:");
        System.out.println(JacksonUtils.toJsonString(branches));

        System.out.println("Tags:");
        System.out.println(JacksonUtils.toJsonString(tags));
    }
}

