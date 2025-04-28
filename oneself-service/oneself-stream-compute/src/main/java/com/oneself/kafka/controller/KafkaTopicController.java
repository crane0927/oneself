package com.oneself.kafka.controller;

import com.oneself.annotation.RequestLogging;
import com.oneself.annotation.RequireLogin;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuhuan
 * date 2025/4/27
 * packageName com.oneself.kafka.controller
 * className KafkaTopicController
 * description Kafka Topic 管理
 * version 1.0
 */
@Tag(name = "Kafka Topic")
@Slf4j
@RequiredArgsConstructor
@RequireLogin
@RequestLogging
@RestController
@RequestMapping("/kafka/topic")
public class KafkaTopicController {
}
