package com.oneself.controller;

import com.oneself.annotation.ApiLog;
import com.oneself.model.dto.*;
import com.oneself.model.vo.QuartzTaskVO;
import com.oneself.resp.Resp;
import com.oneself.service.QuartzJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.quartz.JobDataMap;
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
@RequiredArgsConstructor
@ApiLog
@RestController
@RequestMapping({"/job"})
public class QuartzJobController {

    private final QuartzJobService quartzJobService;

    @Operation(summary = "任务信息列表")
    @GetMapping
    public Resp<List<QuartzTaskVO>> getPageList() {
        return Resp.success(quartzJobService.getPageList());
    }

    @Operation(summary = "创建定时任务")
    @PostMapping("/cron")
    public Resp<Boolean> createCronJob(@RequestBody @Valid CronJobDTO dto) {
        boolean b = quartzJobService.addCronJob(dto.getJobName(),
                dto.getCronExpression(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return Resp.failure("创建失败");
        }
        return Resp.success(Boolean.TRUE);
    }

    @Operation(summary = "创建执行一次的任务")
    @PostMapping("/one-time")
    public Resp<Boolean> createOneJob(@RequestBody @Valid OneJobDTO dto) {
        boolean b = quartzJobService.addOneTimeJob(dto.getJobName(),
                dto.getExecutionTime(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return Resp.failure("创建失败");
        }
        return Resp.success(Boolean.TRUE);
    }

    @Operation(summary = "更新定时任务")
    @PutMapping("/cron")
    public Resp<Boolean> updateCronJob(@RequestBody @Valid CronJobDTO dto) {
        boolean b = quartzJobService.updateCronJob(dto.getJobName(),
                dto.getCronExpression(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return Resp.failure("更新失败");
        }
        return Resp.success(Boolean.TRUE);
    }

    @Operation(summary = "更新执行一次的任务")
    @PutMapping("/one-time")
    public Resp<Boolean> updateOneJob(@RequestBody @Valid OneJobDTO dto) {
        boolean b = quartzJobService.updateOneTimeJob(dto.getJobName(),
                dto.getExecutionTime(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return Resp.failure("更新失败");
        }
        return Resp.success(Boolean.TRUE);
    }

    @Operation(summary = "删除定时任务")
    @DeleteMapping
    public Resp<Boolean> delete(@RequestBody @Valid DeleteJobDTO dto) {
        boolean b = quartzJobService.deleteJob(dto.getJobName(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return Resp.failure("删除失败");
        }
        return Resp.success(Boolean.TRUE);
    }

    @Operation(summary = "暂停任务")
    @PutMapping("/pause")
    public Resp<Boolean> pause(@RequestBody @Valid PauseJobDTO dto) {
        boolean b = quartzJobService.pauseJob(dto.getJobName(),
                dto.getJobGroupName());
        if (!b) {
            return Resp.failure("暂停失败");
        }
        return Resp.success(Boolean.TRUE);
    }

    @Operation(summary = "恢复任务")
    @PutMapping("/resume")
    public Resp<Boolean> resume(@RequestBody @Valid ResumeJobDTO dto) {
        boolean b = quartzJobService.resumeJob(dto.getJobName(),
                dto.getJobGroupName());
        if (!b) {
            return Resp.failure("恢复失败");
        }
        return Resp.success(Boolean.TRUE);
    }

    @Operation(summary = "立即执行任务")
    @PostMapping("/execute")
    public Resp<Boolean> executeImmediately(@RequestBody @Valid ExecuteDTO dto) {
        boolean b = quartzJobService.executeImmediately(dto.getJobName(),
                dto.getJobClassName(),
                ObjectUtils.isNotEmpty(dto.getDataMap()) ? new JobDataMap(dto.getDataMap()) : new JobDataMap(),
                dto.getJobGroupName(),
                dto.getTriggerGroupName(),
                dto.getTriggerPrefix());
        if (!b) {
            return Resp.failure("立即执行失败");
        }
        return Resp.success(true);
    }
}