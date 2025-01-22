package com.oneself.controller;

import com.oneself.annotation.LogRequestDetails;
import com.oneself.model.dto.*;
import com.oneself.model.vo.QuartzTaskVO;
import com.oneself.model.vo.ResponseVO;
import com.oneself.service.QuartzJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Quartz 任务")
@Slf4j
@LogRequestDetails
@RestController
@RequestMapping({"/job"})
public class QuartzJobController {

    private final QuartzJobService quartzJobService;

    @Autowired
    public QuartzJobController(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
    }

    @Operation(summary = "任务信息列表")
    @GetMapping("/get/page/list")
    public ResponseVO<List<QuartzTaskVO>> getPageList() {
        return ResponseVO.success(quartzJobService.getPageList());

    }

    @Operation(summary = "创建定时任务")
    @PostMapping("/create/cron/job")
    public ResponseVO<Boolean> createCronJob(@RequestBody @Valid  CronJobDTO dto) {
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
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "创建执行一次的任务")
    @PostMapping("/create/one/job")
    public ResponseVO<Boolean> createOneJob(@RequestBody @Valid OneJobDTO dto) {
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
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "更新定时任务")
    @PutMapping("/update/cron/job")
    public ResponseVO<Boolean> updateCronJob(@RequestBody @Valid  CronJobDTO dto) {
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
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "更新执行一次的任务")
    @PutMapping("/update/one/job")
    public ResponseVO<Boolean> updateOneJob(@RequestBody @Valid  OneJobDTO dto) {
        Boolean b = quartzJobService.updateOneTimeJob(dto.getJobName(),
                dto.getExecutionTime(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return ResponseVO.failure("更新失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "删除定时任务")
    @DeleteMapping("/delete")
    public ResponseVO<Boolean> delete(@RequestBody @Valid  DeleteJobDTO dto) {
        Boolean b = quartzJobService.deleteJob(dto.getJobName(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return ResponseVO.failure("删除失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "暂停任务")
    @PostMapping("/pause")
    public ResponseVO<Boolean> pause(@RequestBody @Valid  PauseJobDTO dto) {
        Boolean b = quartzJobService.pauseJob(dto.getJobName(),
                dto.getJobGroupName());
        if (!b) {
            return ResponseVO.failure("暂停失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "恢复任务")
    @PostMapping("/resume")
    public ResponseVO<Boolean> resume(@RequestBody @Valid  ResumeJobDTO dto) {
        Boolean b = quartzJobService.resumeJob(dto.getJobName(),
                dto.getJobGroupName());
        if (!b) {
            return ResponseVO.failure("恢复失败");
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    @Operation(summary = "立即执行任务")
    @PostMapping("/execute/immediately")
    public ResponseVO<Boolean> executeImmediately(@RequestBody @Valid  ExecuteDTO dto) {
        Boolean b = quartzJobService.executeImmediately(dto.getJobName(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return ResponseVO.failure("立即执行失败");
        }
        return ResponseVO.success(true);
    }
}