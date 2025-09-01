package com.oneself.script.repository.mongodb;

import com.oneself.script.model.enums.ScriptStatusEnum;
import com.oneself.script.model.mongodb.Script;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.script.repository.mongodb
 * interfaceName ScriptRepository
 * description
 * version 1.0
 */
@Repository
public interface ScriptRepository extends MongoRepository<Script, String> {
    /**
     * 查询所有标记为最新版本的脚本
     *
     * @return 返回最新版本的脚本列表
     */
    List<Script> findByLatestTrue();

    /**
     * 根据脚本状态查询脚本列表
     *
     * @param status 脚本状态枚举，例如 DRAFT, ACTIVE, DEPRECATED
     * @return 返回指定状态的脚本列表
     */
    List<Script> findByStatus(ScriptStatusEnum status);

    /**
     * 根据标签列表查询脚本
     *
     * @param tags 标签列表，任意匹配其中一个标签即可
     * @return 返回包含指定标签的脚本列表
     */
    List<Script> findByTagsIn(List<String> tags);

    /**
     * 根据脚本 ID 和版本号查询指定版本的脚本
     *
     * @param id         脚本 ID
     * @param versionNum 版本号
     * @return 返回匹配的脚本实体，如果不存在返回 null
     */
    Script findByIdAndVersionNum(String id, String versionNum);
}
