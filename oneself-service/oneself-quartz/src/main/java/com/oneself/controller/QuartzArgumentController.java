package com.oneself.controller;

import com.oneself.model.vo.ResponseVO;
import com.oneself.utils.DateFormatUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuhuan
 * date 2024/12/31
 * packageName com.oneself.controller
 * className QuartzArgumentController
 * description Quartz 参数
 * version 1.0
 */
@Tag(name = "Quartz 参数")
@Slf4j
@RestController
@RequestMapping({"/argument"})
public class QuartzArgumentController {

    @Operation(summary = "获取 CRON 表达式后续执行时间")
    @GetMapping("/nextTriggerTime")
    public ResponseVO<List<String>> nextTriggerTime(@Parameter(description = "CRON表达式", required = true, example = "0 0 9/1 * * ? *")
                                                    @RequestParam String scheduleConf,
                                                    @Parameter(description = "后续执行时间数量", required = true, example = "1")
                                                    @RequestParam Integer num) {
        if (num == null || num <= 0 || num > 100) {
            return ResponseVO.failure("参数错误，执行次数范围应在 1 到 100 之间");
        }

        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            CronExpression cronExpression = new CronExpression(scheduleConf);
            for (int i = 0; i < num; i++) {
                lastTime = cronExpression.getNextValidTimeAfter(lastTime);
                if (lastTime == null) {
                    break;
                }
                result.add(DateFormatUtils.SDF_YYYY_MM_DD_HH_MM_SS.format(lastTime));
            }
        } catch (Exception e) {
            log.error("解析 CRON 表达式失败: {}", e.getMessage(), e);
            return ResponseVO.failure("CRON 表达式解析失败，请检查表达式是否正确");
        }

        return ResponseVO.success(result);
    }
}
