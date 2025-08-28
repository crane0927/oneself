package com.oneself.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 分页响应封装类
 *
 * @param <T> 分页记录类型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页响应")
public class PageVO<T> extends ResponseVO<PageVO.DataVO<T>> {

    @Data
    public static class DataVO<T> {
        @Schema(description = "分页记录")
        private List<T> records;
        @Schema(description = "总记录数", example = "100")
        private Long total;
        @Schema(description = "总页数", example = "10")
        private Long pages;
        @Schema(description = "每页记录数", example = "10")
        private Long pageSize;
        @Schema(description = "显示额外标识", example = "false")
        private boolean showRealm;
    }

    private PageVO() {
        super();
    }

    /**
     * 构建分页成功响应
     */
    public static <T> PageVO<T> success(List<T> records, Long total, Long pageSize, Long pages) {
        PageVO<T> pageVO = new PageVO<>();
        DataVO<T> dataVO = new DataVO<>();
        dataVO.setRecords(Optional.ofNullable(records).orElse(Collections.emptyList()));
        dataVO.setTotal(total == null ? 0L : total);
        dataVO.setPageSize(pageSize == null ? 10L : pageSize);
        dataVO.setPages(pages == null ? 0L : pages);
        dataVO.setShowRealm(false);

        pageVO.setData(dataVO);
        pageVO.setMessage("请求成功");
        pageVO.setMsgCode(HttpStatus.OK.value());
        return pageVO;
    }

    /**
     * 构建空数据分页响应
     */
    public static <T> PageVO<T> empty(Long pageSize) {
        return success(Collections.emptyList(), 0L, pageSize, 0L);
    }

    /**
     * 将分页结果转换为分页响应对象
     *
     * @param page   原分页对象
     * @param mapper 转换函数，将 E 类型映射为 V 类型
     * @param <E>    原分页记录类型
     * @param <V>    转换后的记录类型
     * @return PageVO<V>
     */
    public static <E, V> PageVO<V> convert(Page<E> page, Function<E, V> mapper) {
        List<V> records = Optional.ofNullable(page.getRecords())
                .orElse(Collections.emptyList())
                .stream()
                .map(mapper)
                .toList();
        return success(records, page.getTotal(), page.getSize(), page.getPages());
    }

    /**
     * 将分页结果直接返回原类型记录
     */
    public static <T> PageVO<T> from(Page<T> page) {
        return success(page.getRecords(), page.getTotal(), page.getSize(), page.getPages());
    }
}
