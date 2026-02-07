package com.oneself.common.core.pagination;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.pagination
 * interfaceName PageResult
 * description 统一分页接口，适配不同分页源
 * version 1.0
 */
public interface PageWrapper<T> {
    List<T> getRecords();

    long getTotal();

    long getPages();

    long getSize();
}

