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
    Boolean addCronJob(String jobName, String cron, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    Boolean addOneTimeJob(String jobName, LocalDateTime executeTime, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    Boolean updateOneTimeJob(String jobName, LocalDateTime newExecuteTime, String jobGroup, String triggerGroup, String triggerPer);

    Boolean updateCronJob(String jobName, String newCron, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    Boolean deleteJob(String jobName, String jobGroup, String triggerGroup, String triggerPer);

    Boolean pauseJob(String jobName, String jobGroup);

    Boolean resumeJob(String jobName, String jobGroup);

    Boolean executeImmediately(String jobName, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer);

    List<QuartzTaskVO> getPageList();
}
