package com.oneself.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author liuhuan
 * date 2025/1/4
 * packageName com.oneself.job
 * className HelloJob
 * description
 * version 1.0
 */
@Slf4j
public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        jobDataMap.forEach((k, v) -> System.out.println(k + ":" + v));
        log.info("HelloJob is running");

    }
}
