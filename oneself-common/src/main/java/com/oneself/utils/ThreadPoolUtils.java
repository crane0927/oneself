package com.oneself.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuhuan
 * date 2025/2/13
 * packageName com.oneself.utils
 * className ThreadPoolUtils
 * description 线程池工具类
 * version 1.0
 */
@Slf4j
public class ThreadPoolUtils {

    /**
     * 单例模式
     */
    private static ThreadPoolUtils instance;

    /**
     * 线程池实例
     */
    private final ThreadPoolExecutor threadPool;

    /**
     * 私有构造函数
     */
    private ThreadPoolUtils() {
        // 定义核心线程数、最大线程数、空闲线程存活时间、任务队列和线程工厂
        threadPool = new ThreadPoolExecutor(
                4,                    // 核心线程数
                10,                               // 最大线程数
                60,                               // 空闲线程存活时间（秒）
                TimeUnit.SECONDS,                 // 时间单位
                new LinkedBlockingQueue<>(100),   // 任务队列
                new CustomThreadFactory(),        // 自定义线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
    }

    /**
     * 获取单例实例
     *
     * @return ThreadPoolUtils
     */
    public static synchronized ThreadPoolUtils getInstance() {
        if (instance == null) {
            instance = new ThreadPoolUtils();
        }
        return instance;
    }

    // 提交任务
    public void submitTask(Runnable task) {
        if (task != null) {
            threadPool.execute(task);
        }
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取线程池状态
     */
    public void printThreadPoolStatus() {
        log.info("核心线程数: {}", threadPool.getCorePoolSize());
        log.info("最大线程数: {}", threadPool.getMaximumPoolSize());
        log.info("当前线程数: {}", threadPool.getPoolSize());
        log.info("任务队列大小: {}", threadPool.getQueue().size());
        log.info("已完成任务数: {}", threadPool.getCompletedTaskCount());

    }

    /**
     * 自定义线程工厂
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("oneself-" + threadCount.getAndIncrement());
            return thread;
        }
    }

    // 测试主方法
    public static void main(String[] args) {
        ThreadPoolUtils threadPoolManager = ThreadPoolUtils.getInstance();

        // 提交任务
        for (int i = 1; i <= 20; i++) {
            int taskId = i;
            threadPoolManager.submitTask(() -> {
                log.info(Thread.currentThread().getName() + " 正在执行任务 " + taskId);
                try {
                    Thread.sleep(1000); // 模拟任务耗时
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 打印线程池状态
        threadPoolManager.printThreadPoolStatus();

        // 等待一段时间后关闭线程池
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        threadPoolManager.shutdown();
    }
}
