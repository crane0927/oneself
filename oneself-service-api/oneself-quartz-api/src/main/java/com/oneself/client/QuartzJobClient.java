package com.oneself.client;

import com.oneself.model.dto.*;
import com.oneself.model.vo.ResponseVO;
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
    @PostMapping("/create/cron/job")
    ResponseVO<Boolean> createCronJob(@RequestBody CronJobDTO dto);

    @Operation(summary = "创建执行一次的任务")
    @PostMapping("/create/one/job")
    ResponseVO<Boolean> createOneJob(@RequestBody OneJobDTO dto);

    @Operation(summary = "更新定时任务")
    @PutMapping("/update/cron/job")
    ResponseVO<Boolean> updateCronJob(@RequestBody CronJobDTO dto);

    @Operation(summary = "更新执行一次的任务")
    @PutMapping("/update/one/job")
    ResponseVO<Boolean> updateOneJob(@RequestBody OneJobDTO dto);

    @Operation(summary = "删除定时任务")
    @DeleteMapping("/delete")
    ResponseVO<Boolean> delete(@RequestBody DeleteJobDTO dto);

    @Operation(summary = "暂停任务")
    @PostMapping("/pause")
    ResponseVO<Boolean> pause(@RequestBody PauseJobDTO dto);

    @Operation(summary = "恢复任务")
    @PostMapping("/resume")
    ResponseVO<Boolean> resume(@RequestBody ResumeJobDTO dto);

    @Operation(summary = "立即执行任务")
    @PostMapping("/execute/immediately")
    ResponseVO<Boolean> executeImmediately(@RequestBody ExecuteDTO dto);
}