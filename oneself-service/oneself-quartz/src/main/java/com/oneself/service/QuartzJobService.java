package com.oneself.service;

import com.oneself.model.vo.QuartzTaskVO;
import org.quartz.JobDataMap;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/1/4
 * packageName com.oneself.service
 * interfaceName QuartzJobService
 * description Quartz 任务接口
 * version 1.0
 */
public interface QuartzJobService {
    boolean addCronJob(String jobName, String cron, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    boolean addOneTimeJob(String jobName, LocalDateTime executeTime, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    boolean updateOneTimeJob(String jobName, LocalDateTime newExecuteTime, String jobGroup, String triggerGroup, String triggerPer);

    boolean updateCronJob(String jobName, String newCron, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    boolean deleteJob(String jobName, String jobGroup, String triggerGroup, String triggerPer);

    boolean pauseJob(String jobName, String jobGroup);

    boolean resumeJob(String jobName, String jobGroup);

    boolean executeImmediately(String jobName, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    List<QuartzTaskVO> getPageList();
}
