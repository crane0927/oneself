package com.oneself.utils;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author liuhuan
 * date 2024/12/10
 * packageName com.example.oneself.common.utils
 * className QuartzCommonUtils
 * description Quartz 定时任务工具类
 * version 1.0
 */
@Slf4j
@Component
public class QuartzCommonUtils {

    private final Scheduler scheduler;

    public QuartzCommonUtils(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 检查指定任务是否存在
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return 如果任务存在返回 true，否则返回 false
     * @throws SchedulerException 调度异常
     */
    private boolean isJobExists(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        return scheduler.checkExists(jobKey);
    }

    /**
     * 记录任务操作日志
     *
     * @param action   操作类型（如：新增、修改、删除）
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @param success  操作是否成功
     */
    private void logJobAction(String action, String jobName, String jobGroup, boolean success) {
        if (success) {
            log.info("【{}】成功，jobName: {}，jobGroup: {}", action, jobName, jobGroup);
        } else {
            log.error("【{}】失败，jobName: {}，jobGroup: {}", action, jobName, jobGroup);
        }
    }

    /**
     * 创建触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器组
     * @param startTime    触发器开始时间
     * @param cron         Cron 表达式，如果为 null，则创建一次性触发器
     * @return 触发器对象
     */
    private Trigger createTrigger(String triggerName, String triggerGroup, Date startTime, String cron) {
        if (cron != null) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            return TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, triggerGroup)
                    .withSchedule(cronScheduleBuilder)
                    .build();
        } else {
            return TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, triggerGroup)
                    .startAt(startTime)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                    .build();
        }
    }

    /**
     * 将 LocalDateTime 转换为 Date 对象
     *
     * @param localDateTime LocalDateTime 对象
     * @return 转换后的 Date 对象
     */
    private static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    /**
     * 根据类的全限定名称获取 Job 类的 Class 对象
     *
     * @param className 类的全限定名称
     * @return 对应的 Class<? extends Job> 类型
     * @throws RuntimeException 如果类加载失败抛出异常
     */
    private static Class<? extends Job> getJobClass(String className) {
        try {
            return (Class<? extends Job>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("【获取 Job 类】失败，className 为：{}", className, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加一个 Cron 定时任务
     *
     * @param jobName      定时任务名称
     * @param cron         Cron 表达式
     * @param jobClassName 任务类全限定名称
     * @param dataMap      任务的附加数据
     * @param jobGroup     任务组
     * @param triggerGroup 触发器组
     * @param triggerPer   触发器前缀
     * @return 成功返回 true，失败返回 false
     */
    public Boolean addCronJob(String jobName, String cron, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer) {
        try {
            if (isJobExists(jobName, jobGroup)) {
                log.error("【新增定时任务】已存在该作业，jobKey 为：{}", jobName);
                return Boolean.FALSE;
            }

            // 构建 JobDetail
            JobBuilder jobBuilder = JobBuilder.newJob(getJobClass(jobClassName))
                    .withIdentity(jobName, jobGroup);

            // 判断 dataMap 是否为空，只有非空时才设置 JobDataMap
            if (!ObjectUtils.isEmpty(dataMap)) {
                jobBuilder.setJobData(dataMap);
            }

            // 最后构建 JobDetail
            JobDetail job = jobBuilder.build();


            // 构建 Cron 表达式触发器
            Trigger trigger = createTrigger(triggerPer + jobName, triggerGroup, null, cron);

            // 调度任务
            scheduler.scheduleJob(job, trigger);
            logJobAction("新增定时任务", jobName, jobGroup, true);
            return Boolean.TRUE;
        } catch (SchedulerException e) {
            log.error("【新增定时任务】失败，报错：{}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 添加一个只执行一次的定时任务
     *
     * @param jobName      定时任务名称
     * @param executeTime  执行时间
     * @param jobClassName 任务类全限定名称
     * @param dataMap      任务的附加数据
     * @param jobGroup     任务组
     * @param triggerGroup 触发器组
     * @param triggerPer   触发器前缀
     * @return 成功返回 true，失败返回 false
     */
    public Boolean addOneTimeJob(String jobName, LocalDateTime executeTime, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer) {
        try {
            if (isJobExists(jobName, jobGroup)) {
                log.error("【添加单次定时任务】已存在该作业，jobKey 为：{}", jobName);
                return Boolean.FALSE;
            }

            // 构建 JobDetail
            JobBuilder jobBuilder = JobBuilder.newJob(getJobClass(jobClassName))
                    .withIdentity(jobName, jobGroup);

            // 判断 dataMap 是否为空，只有非空时才设置 JobDataMap
            if (!ObjectUtils.isEmpty(dataMap)) {
                jobBuilder.setJobData(dataMap);
            }

            // 最后构建 JobDetail
            JobDetail job = jobBuilder.build();

            // 构建 SimpleTrigger
            Trigger trigger = createTrigger(triggerPer + jobName, triggerGroup, convertLocalDateTimeToDate(executeTime), null);

            // 调度任务
            scheduler.scheduleJob(job, trigger);
            logJobAction("添加单次定时任务", jobName, jobGroup, true);
            return Boolean.TRUE;
        } catch (SchedulerException e) {
            log.error("【添加单次定时任务】失败，报错：{}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 修改单次任务的执行时间
     *
     * @param jobName        作业名称
     * @param newExecuteTime 新的执行时间
     * @param jobGroup       任务组
     * @param triggerGroup   触发器组
     * @param triggerPer     触发器前缀
     * @return 修改结果，成功返回 true
     */
    public Boolean updateOneTimeJob(String jobName, LocalDateTime newExecuteTime, String jobGroup, String triggerGroup, String triggerPer) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerPer + jobName, triggerGroup);

            if (!isJobExists(jobName, jobGroup)) {
                log.error("【修改单次任务】作业不存在，jobKey 为：{}", jobKey);
                return Boolean.FALSE;
            }

            // 获取原有 Trigger
            Trigger oldTrigger = scheduler.getTrigger(triggerKey);
            if (oldTrigger == null) {
                log.info("【修改单次任务】触发器不存在，triggerKey 为：{}", triggerKey);
                return Boolean.FALSE;
            }

            // 构建新的 Trigger
            Trigger newTrigger = createTrigger(triggerPer + jobName, triggerGroup, convertLocalDateTimeToDate(newExecuteTime), null);

            // 替换旧的 Trigger
            scheduler.rescheduleJob(triggerKey, newTrigger);
            logJobAction("修改单次任务", jobName, jobGroup, true);
            return Boolean.TRUE;
        } catch (SchedulerException e) {
            log.error("【修改单次任务】失败，jobName 为：{}，报错：{}", jobName, e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 修改一个 Cron 定时任务的执行计划
     *
     * @param jobName      定时任务名称
     * @param newCron      新的 Cron 表达式
     * @param jobClassName 任务类全限定名称
     * @param dataMap      任务的附加数据
     * @param jobGroup     任务组
     * @param triggerGroup 触发器组
     * @param triggerPer   触发器前缀
     * @return 成功返回 true，失败返回 false
     */
    public Boolean updateCronJob(String jobName, String newCron, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerPer + jobName, triggerGroup);

            if (!isJobExists(jobName, jobGroup)) {
                log.error("【修改定时任务】作业不存在，jobKey 为：{}", jobKey);
                return Boolean.FALSE;
            }


            // 构建 JobDetail
            JobBuilder jobBuilder = JobBuilder.newJob(getJobClass(jobClassName))
                    .withIdentity(jobKey);

            // 判断 dataMap 是否为空，只有非空时才设置 JobDataMap
            if (!ObjectUtils.isEmpty(dataMap)) {
                jobBuilder.setJobData(dataMap);
            }

            // 最后构建 JobDetail
            JobDetail newJobDetail = jobBuilder.storeDurably(true).build();

            // 创建新的 Cron 表达式触发器
            Trigger newTrigger = createTrigger(triggerPer + jobName, triggerGroup, null, newCron);

            // 删除旧的任务，添加新的任务
            scheduler.deleteJob(jobKey);
            scheduler.scheduleJob(newJobDetail, newTrigger);
            log.info("【修改定时任务】成功，jobName 为：{}，新的 Cron 为：{}", jobName, newCron);
            return Boolean.TRUE;
        } catch (SchedulerException e) {
            log.error("【修改定时任务】失败，jobName 为：{}，报错：{}", jobName, e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 删除定时任务
     *
     * @param jobName      作业名称
     * @param jobGroup     任务组
     * @param triggerGroup 触发器组
     * @param triggerPer   触发器前缀
     * @return 成功返回 true，失败返回 false
     */
    public Boolean deleteJob(String jobName, String jobGroup, String triggerGroup, String triggerPer) {
        try {
            if (!isJobExists(jobName, jobGroup)) {
                log.error("【删除定时任务】作业不存在，jobKey 为：{}", jobName);
                return Boolean.FALSE;
            }

            TriggerKey triggerKey = TriggerKey.triggerKey(triggerPer + jobName, triggerGroup);
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            boolean isDeleted = scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));

            logJobAction("删除任务", jobName, jobGroup, isDeleted);
            return isDeleted;
        } catch (SchedulerException e) {
            log.error("【删除定时任务】失败，jobName: {}，报错：{}", jobName, e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 暂停指定的定时任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return 暂停结果，成功返回 true
     */
    public Boolean pauseJob(String jobName, String jobGroup) {
        try {
            if (!isJobExists(jobName, jobGroup)) {
                log.error("【暂停定时任务】作业不存在，jobKey 为：{}", jobName);
                return Boolean.FALSE;
            }
            scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
            log.info("【暂停定时任务】成功，jobName 为：{}", jobName);
            return Boolean.TRUE;
        } catch (SchedulerException e) {
            log.error("【暂停定时任务】失败，jobName 为：{}，报错：{}", jobName, e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 恢复指定的定时任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return 恢复结果，成功返回 true
     */
    public Boolean resumeJob(String jobName, String jobGroup) {
        try {
            if (!isJobExists(jobName, jobGroup)) {
                log.error("【恢复定时任务】作业不存在，jobKey 为：{}", jobName);
                return Boolean.FALSE;
            }
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
            log.info("【恢复定时任务】成功，jobName 为：{}", jobName);
            return Boolean.TRUE;
        } catch (SchedulerException e) {
            log.error("【恢复定时任务】失败，jobName 为：{}，报错：{}", jobName, e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 立即执行一次任务（不定时）
     *
     * @param jobName      定时任务名称
     * @param jobClassName 任务类全限定名称
     * @param dataMap      任务的附加数据
     * @param jobGroup     任务组
     * @param triggerGroup 触发器组
     * @param triggerPer   触发器前缀
     * @return 成功返回 true，失败返回 false
     */
    public Boolean executeImmediately(String jobName, String jobClassName, JobDataMap dataMap, String jobGroup, String triggerGroup, String triggerPer) {
        try {
            if (!isJobExists(jobName, jobGroup)) {
                log.error("【立即执行一次任务】作业不存在，jobKey 为：{}", jobName);
                return Boolean.FALSE;
            }

            // 创建 JobKey，使用任务名称和任务组
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

            // 构建 JobDetail
            JobBuilder jobBuilder = JobBuilder.newJob(getJobClass(jobClassName))
                    .withIdentity(jobKey);

            // 判断 dataMap 是否为空，只有非空时才设置 JobDataMap
            if (!ObjectUtils.isEmpty(dataMap)) {
                jobBuilder.setJobData(dataMap);
            }

            // 最后构建 JobDetail
            JobDetail job = jobBuilder.storeDurably(true).build();


            // 创建触发器，立即执行任务
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(TriggerKey.triggerKey(triggerPer + jobName, triggerGroup))
                    .startNow() // 设置触发器立即执行任务
                    .build();

            // 调度任务
            scheduler.scheduleJob(job, trigger);

            // 启动调度器（如果调度器没有启动的话）
            if (!scheduler.isStarted()) {
                scheduler.start();
            }

            return Boolean.TRUE;
        } catch (SchedulerException e) {
            log.error("【立即执行一次任务，不定时】失败，报错：", e);
        }
        return Boolean.FALSE;
    }

}
