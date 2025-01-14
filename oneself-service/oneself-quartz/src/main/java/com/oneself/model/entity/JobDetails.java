package com.oneself.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2025/1/4
 * packageName com.oneself.model.entity
 * className JobDetails
 * description 任务详细信息表
 * version 1.0
 */
@Data
@TableName("qrtz_job_details")
public class JobDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private String schedName;       // 调度器名称

    private String jobName;         // 任务名称

    private String jobGroup;        // 任务组名称

    private String description;     // 任务描述

    private String jobClassName;    // 任务类的完整类名

    private String isDurable;       // 是否持久化任务（1 表示是）

    private String isNonconcurrent; // 是否不允许并发执行（1 表示是）

    private String isUpdateData;    // 是否更新数据（1 表示是）

    private String requestsRecovery; // 是否恢复时重新执行（1 表示是）

    private byte[] jobData;         // 存储任务的序列化数据（如参数）
}
