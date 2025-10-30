package com.oneself.resp;

import com.oneself.pagination.PageWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author liuhuan
 * date 2024/12/9
 * packageName com.oneself.resp
 * className PageResp
 * description 分页响应封装类
 * version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分页响应")
public class PageResp<T> extends Resp<PageResp.Data<T>> {

    @lombok.Data
    @Schema(description = "分页数据对象")
    public static class Data<T> {
        @Schema(description = "分页记录列表")
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

    @Schema(description = "分页数据")
    private Data<T> data;

    @Schema(description = "消息描述")
    private String message;

    @Schema(description = "状态码", example = "200")
    private int msgCode;

    private PageResp() {
        super();
    }

    @Schema(description = "构建成功分页响应")
    public static <T> PageResp<T> success(
            @Schema(description = "分页记录") List<T> records,
            @Schema(description = "总记录数") Long total,
            @Schema(description = "每页记录数") Long pageSize,
            @Schema(description = "总页数") Long pages) {
        PageResp<T> vo = new PageResp<>();
        Data<T> data = new Data<>();
        data.setRecords(Optional.ofNullable(records).orElse(Collections.emptyList()));
        data.setTotal(total == null ? 0L : total);
        data.setPageSize(pageSize == null ? 10L : pageSize);
        data.setPages(pages == null ? 0L : pages);
        data.setShowRealm(false);
        vo.setData(data);
        vo.setMessage("请求成功");
        vo.setMsgCode(200);
        return vo;
    }

    /**
     * 将任意实现 PageWrapper 的分页对象转换为 PageVO
     */
    @Schema(description = "将分页对象转换为 PageVO")
    public static <E, V> PageResp<V> convert(
            @Schema(description = "分页对象") PageWrapper<E> page,
            @Schema(description = "映射函数，将 E 类型映射为 V 类型") Function<E, V> mapper) {
        List<V> records = Optional.ofNullable(page.getRecords())
                .orElse(Collections.emptyList())
                .stream()
                .map(mapper)
                .toList();
        return success(records, page.getTotal(), page.getSize(), page.getPages());
    }

    /**
     * 将 PageWrapper 直接返回原类型记录
     */
    @Schema(description = "将分页对象直接返回原类型记录")
    public static <T> PageResp<T> from(
            @Schema(description = "分页对象") PageWrapper<T> page) {
        return success(page.getRecords(), page.getTotal(), page.getSize(), page.getPages());
    }
}
