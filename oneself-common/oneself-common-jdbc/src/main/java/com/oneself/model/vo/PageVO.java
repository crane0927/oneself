package com.oneself.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author liuhuan
 * date 2024/12/9
 * packageName com.oneself.common.model.vo
 * className PageVO<T>
 * description 分页响应封装类（支持 MyBatis-Plus 与 MongoDB）
 * version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分页响应（支持 MyBatis-Plus 与 MongoDB）")
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

    public static <T> PageVO<T> empty(Long pageSize) {
        return success(Collections.emptyList(), 0L, pageSize, 0L);
    }

    /**
     * 将 MyBatis-Plus Page 转换为 PageVO
     */
    public static <E, V> PageVO<V> convertMybatis(com.baomidou.mybatisplus.extension.plugins.pagination.Page<E> page,
                                                  Function<E, V> mapper) {
        List<V> records = Optional.ofNullable(page.getRecords())
                .orElse(Collections.emptyList())
                .stream().map(mapper).toList();
        return success(records, page.getTotal(), page.getSize(), page.getPages());
    }

    /**
     * 将 Spring Data MongoDB Page 转换为 PageVO
     */
    public static <E, V> PageVO<V> convertMongo(org.springframework.data.domain.Page<E> page,
                                                Function<E, V> mapper) {
        List<V> records = Optional.of(page.getContent())
                .orElse(Collections.emptyList())
                .stream().map(mapper).toList();
        return success(records, page.getTotalElements(), (long) page.getSize(), (long) page.getTotalPages());
    }

    /**
     * 将 MyBatis-Plus Page 直接返回原类型记录
     */
    public static <T> PageVO<T> fromMybatis(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return success(page.getRecords(), page.getTotal(), page.getSize(), page.getPages());
    }

    /**
     * 将 MongoDB Page 直接返回原类型记录
     */
    public static <T> PageVO<T> fromMongo(org.springframework.data.domain.Page<T> page) {
        return success(page.getContent(), page.getTotalElements(), (long) page.getSize(), (long) page.getTotalPages());
    }
}
