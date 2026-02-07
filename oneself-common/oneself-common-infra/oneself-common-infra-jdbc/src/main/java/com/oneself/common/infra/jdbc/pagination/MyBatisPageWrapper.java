package com.oneself.common.infra.jdbc.pagination;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oneself.common.core.pagination.PageWrapper;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.pagination
 * interfaceName MyBatisPageWrapper
 * description
 * version 1.0
 */
public class MyBatisPageWrapper<T> implements PageWrapper<T> {

    private final Page<T> page;

    public MyBatisPageWrapper(Page<T> page) {
        this.page = page;
    }

    @Override
    public List<T> getRecords() {
        return page.getRecords();
    }

    @Override
    public long getTotal() {
        return page.getTotal();
    }

    @Override
    public long getPages() {
        return page.getPages();
    }

    @Override
    public long getSize() {
        return page.getSize();
    }
}