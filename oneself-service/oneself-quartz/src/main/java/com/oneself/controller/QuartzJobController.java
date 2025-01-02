package com.oneself.controller;

import com.oneself.model.dto.*;
import com.oneself.model.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.controller
 * className QuartzJobController
 * description Quartz 任务接口
 * version 1.0
 */
@Api(tags = "Quartz 任务")
@Slf4j
@RestController
@RequestMapping({"/job"})
public class QuartzJobController {

    @ApiOperation(value = "创建定时任务")
    @PostMapping("/create/cron/job")
    public ResponseVO<Boolean> createCronJob(@RequestBody CronJobDTO dto) {
        return ResponseVO.success(true);
    }

    @ApiOperation(value = "创建执行一次的任务")
    @PostMapping("/create/one/job")
    public ResponseVO<Boolean> createOneJob(@RequestBody OneJobDTO dto) {
        return ResponseVO.success(true);
    }

    @ApiOperation(value = "更新定时任务")
    @PutMapping("/update/cron/job/{id}")
    public ResponseVO<Boolean> updateCronJob(@PathVariable Long id, @RequestBody CronJobDTO dto) {
        return ResponseVO.success(true);
    }

    @ApiOperation(value = "更新执行一次的任务")
    @PutMapping("/update/one/job/{id}")
    public ResponseVO<Boolean> updateOneJob(@PathVariable Long id, @RequestBody OneJobDTO dto) {
        return ResponseVO.success(true);
    }

    @ApiOperation(value = "删除定时任务")
    @DeleteMapping("/delete/{id}")
    public ResponseVO<Boolean> delete(@PathVariable Long id, @RequestBody DeleteJobDTO dto) {
        return ResponseVO.success(true);
    }

    @ApiOperation(value = "暂停任务")
    @PostMapping("/pause/{id}")
    public ResponseVO<Boolean> pause(@PathVariable Long id, @RequestBody PauseJobDTO dto) {
        return ResponseVO.success(true);
    }

    @ApiOperation(value = "恢复任务")
    @PostMapping("/resume/{id}")
    public ResponseVO<Boolean> resume(@PathVariable Long id, @RequestBody ResumeJobDTO dto) {
        return ResponseVO.success(true);
    }

    @ApiOperation(value = "立即执行任务")
    @PostMapping("/execute/{id}/immediately")
    public ResponseVO<Boolean> executeImmediately(@PathVariable Long id, @RequestBody ExecuteDTO dto) {
        return ResponseVO.success(true);
    }
}