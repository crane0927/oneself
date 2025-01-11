package com.oneself.controller;

import com.oneself.model.dto.*;
import com.oneself.model.vo.QuartzTaskVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.service.QuartzJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private final QuartzJobService quartzJobService;

    @Autowired
    public QuartzJobController(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
    }

    @ApiOperation(value = "任务信息列表")
    @GetMapping("/get/page/list")
    public ResponseVO<List<QuartzTaskVO>> getPageList() {
        return ResponseVO.success(quartzJobService.getPageList());

    }

    @ApiOperation(value = "创建定时任务")
    @PostMapping("/create/cron/job")
    public ResponseVO<Boolean> createCronJob(@RequestBody CronJobDTO dto) {
        Boolean b = quartzJobService.addCronJob(dto.getJobName(),
                dto.getCronExpression(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return ResponseVO.failure("创建失败");
        }
        return ResponseVO.success(Boolean.TRUE, "创建成功");
    }

    @ApiOperation(value = "创建执行一次的任务")
    @PostMapping("/create/one/job")
    public ResponseVO<Boolean> createOneJob(@RequestBody OneJobDTO dto) {
        Boolean b = quartzJobService.addOneTimeJob(dto.getJobName(),
                dto.getExecutionTime(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return ResponseVO.failure("创建失败");
        }
        return ResponseVO.success(Boolean.TRUE, "创建成功");
    }

    @ApiOperation(value = "更新定时任务")
    @PutMapping("/update/cron/job/{id}")
    public ResponseVO<Boolean> updateCronJob(@PathVariable Long id, @RequestBody CronJobDTO dto) {
        Boolean b = quartzJobService.updateCronJob(dto.getJobName(),
                dto.getCronExpression(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return ResponseVO.failure("更新失败");
        }
        return ResponseVO.success(Boolean.TRUE, "更新成功");
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