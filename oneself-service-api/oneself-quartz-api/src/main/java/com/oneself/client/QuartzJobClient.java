package com.oneself.client;

import com.oneself.model.dto.*;
import com.oneself.resp.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.client
 * interfaceName QuartzJobClient
 * description Quartz 任务接口
 * version 1.0
 */
@Tag(name = "Quartz 任务")
@FeignClient(value = "oneself-job-service", path = "/job")
public interface QuartzJobClient {

    @Operation(summary = "创建定时任务")
    @PostMapping("/cron")
    Resp<Boolean> createCronJob(@RequestBody CronJobDTO dto);

    @Operation(summary = "创建执行一次的任务")
    @PostMapping("/one-time")
    Resp<Boolean> createOneJob(@RequestBody OneJobDTO dto);

    @Operation(summary = "更新定时任务")
    @PutMapping("/cron")
    Resp<Boolean> updateCronJob(@RequestBody CronJobDTO dto);

    @Operation(summary = "更新执行一次的任务")
    @PutMapping("/one-time")
    Resp<Boolean> updateOneJob(@RequestBody OneJobDTO dto);

    @Operation(summary = "删除定时任务")
    @DeleteMapping
    Resp<Boolean> delete(@RequestBody DeleteJobDTO dto);

    @Operation(summary = "暂停任务")
    @PutMapping("/pause")
    Resp<Boolean> pause(@RequestBody PauseJobDTO dto);

    @Operation(summary = "恢复任务")
    @PutMapping("/resume")
    Resp<Boolean> resume(@RequestBody ResumeJobDTO dto);

    @Operation(summary = "立即执行任务")
    @PostMapping("/execute")
    Resp<Boolean> executeImmediately(@RequestBody ExecuteDTO dto);
}