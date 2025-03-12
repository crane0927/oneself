package com.oneself.service.impl;

import com.oneself.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuhuan
 * date 2025/3/12
 * packageName com.oneself.service.impl
 * className DemoServiceImpl
 * description 样例接口实现类
 * version 1.0
 */
@Slf4j
@Service
public class DemoServiceImpl implements DemoService {

    /**
     * 线程安全的 ConcurrentHashMap
     */
    private static final Map<Integer, String> INTEGER_STRING_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 线程安全的 CopyOnWriteArrayList
     */
    private static final List<String> STRING_COPY_ONWRITE_ARRAY_LIST = new CopyOnWriteArrayList<>();

    /**
     * 用于修改 INTEGER_STRING_CONCURRENT_HASH_MAP 的锁
     */
    private static final Lock MAP_LOCK = new ReentrantLock();
    /**
     * 用于修改 STRING_COPY_ONWRITE_ARRAY_LIST 的锁
     */
    private static final Lock LIST_LOCK = new ReentrantLock();

    /**
     * 根据 key 从缓存中获取值，如果缓存中没有，则从数据库中获取
     *
     * @param key key
     * @return value
     */
    private String getValueByKey(Integer key) {
        // 从缓存中获取
        String value = INTEGER_STRING_CONCURRENT_HASH_MAP.get(key);
        // 如果缓存中没有，则从数据库中获取
        if (StringUtils.isBlank(value)) {
            // 加锁
            MAP_LOCK.lock();
            try {
                if (StringUtils.isBlank(value)) {
                    // TODO 根据实际情况获取结果
                    value = "crane";
                    INTEGER_STRING_CONCURRENT_HASH_MAP.put(key, value);
                }
            } finally {
                // 释放锁
                MAP_LOCK.unlock();
            }
        }
        return value;
    }

    /**
     * 根据 index 从缓存中获取值，如果缓存中没有，则从数据库中获取
     *
     * @param index 索引
     * @return value
     */
    private String getValueByIndex(Integer index) {
        String value = STRING_COPY_ONWRITE_ARRAY_LIST.get(index);
        if (StringUtils.isBlank(value)) {
            LIST_LOCK.lock();
            try {
                if (StringUtils.isBlank(value)) {
                    // TODO 根据实际情况获取结果
                    value = "crane";
                    STRING_COPY_ONWRITE_ARRAY_LIST.add(value);
                }
            } finally {
                LIST_LOCK.unlock();
            }
        }
        return value;
    }

}
