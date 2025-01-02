package com.oneself.client;

import com.oneself.model.dto.*;
import com.oneself.model.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "Quartz 任务")
@FeignClient(value = "oneself-job-service", path = "/job")
public interface QuartzJobClient {

    @ApiOperation(value = "创建定时任务")
    @PostMapping("/create/cron/job")
    ResponseVO<Boolean> createCronJob(@RequestBody CronJobDTO dto);

    @ApiOperation(value = "创建执行一次的任务")
    @PostMapping("/create/one/job")
    ResponseVO<Boolean> createOneJob(@RequestBody OneJobDTO dto);

    @ApiOperation(value = "更新定时任务")
    @PutMapping("/update/cron/job/{id}")
    ResponseVO<Boolean> updateCronJob(@PathVariable("id") Long id, @RequestBody CronJobDTO dto);

    @ApiOperation(value = "更新执行一次的任务")
    @PutMapping("/update/one/job/{id}")
    ResponseVO<Boolean> updateOneJob(@PathVariable("id") Long id, @RequestBody OneJobDTO dto);

    @ApiOperation(value = "删除定时任务")
    @DeleteMapping("/delete/{id}")
    ResponseVO<Boolean> delete(@PathVariable("id") Long id, @RequestBody DeleteJobDTO dto);

    @ApiOperation(value = "暂停任务")
    @PostMapping("/pause/{id}")
    ResponseVO<Boolean> pause(@PathVariable("id") Long id, @RequestBody PauseJobDTO dto);

    @ApiOperation(value = "恢复任务")
    @PostMapping("/resume/{id}")
    ResponseVO<Boolean> resume(@PathVariable("id") Long id, @RequestBody ResumeJobDTO dto);

    @ApiOperation(value = "立即执行任务")
    @PostMapping("/execute/{id}/immediately")
    ResponseVO<Boolean> executeImmediately(@PathVariable("id") Long id, @RequestBody ExecuteDTO dto);
}