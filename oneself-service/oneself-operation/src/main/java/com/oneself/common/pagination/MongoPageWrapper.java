package com.oneself.common.pagination;

import com.oneself.pagination.PageWrapper;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/1
 * packageName com.oneself.common.pagination
 * interfaceName MongoPageWrapper
 * description
 * version 1.0
 */
public class MongoPageWrapper<T> implements PageWrapper<T> {

    private final Page<T> page;

    public MongoPageWrapper(Page<T> page) {
        this.page = page;
    }

    @Override
    public List<T> getRecords() {
        return page.getContent();
    }

    @Override
    public long getTotal() {
        return page.getTotalElements();
    }

    @Override
    public long getPages() {
        return page.getTotalPages();
    }

    @Override
    public long getSize() {
        return page.getSize();
    }
}
