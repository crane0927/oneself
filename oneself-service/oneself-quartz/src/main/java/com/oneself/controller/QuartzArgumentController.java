package com.oneself.controller;

import com.oneself.model.dto.NextTriggerTimeDTO;
import com.oneself.model.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
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
@Api(tags = "Quartz 参数")
@Slf4j
@RestController
@RequestMapping({"/argument"})
public class QuartzArgumentController {

    /**
     * 日期格式化
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @ApiOperation(value = "获取 CRON 表达式后续执行时间")
    @GetMapping("/nextTriggerTime")
    public ResponseVO<List<String>> nextTriggerTime(NextTriggerTimeDTO dto) {
        Integer num = dto.getNum();
        String scheduleConf = dto.getScheduleConf();
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
                result.add(DATE_FORMAT.format(lastTime));
            }
        } catch (Exception e) {
            log.error("解析 CRON 表达式失败: {}", e.getMessage(), e);
            return ResponseVO.failure("CRON 表达式解析失败，请检查表达式是否正确");
        }

        return ResponseVO.success(result);
    }
}
