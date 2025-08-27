package com.oneself.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;


/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.common.model.vo
 * className PageVO
 * description 分页查询返回结果
 * version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageVO<T> extends ResponseVO<PageVO.DataVO<T>> {

    @Data
    public static class DataVO<T> {
        @Schema(description = "分页数据")
        private List<T> records;
        @Schema(description = "总数", example = "100")
        private Long rowCount;
        @Schema(description = "总页数", example = "10")
        private Long totalPage;
        @Schema(description = "页面大小", example = "10")
        private Long pageSize;
        @Schema(description = "", example = "false")
        private boolean showRealm;
    }

    private PageVO() {
        super();
    }

    /**
     * 成功响应方法，用于返回有数据的分页信息
     *
     * @param records   分页记录
     * @param rowCount  总记录数
     * @param pageSize  每页大小
     * @param totalPage 总页数
     * @param <T>       数据类型
     * @return 成功的分页响应
     */
    public static <T> PageVO<T> success(List<T> records, Long rowCount, Long pageSize, Long totalPage) {
        PageVO<T> pageVO = new PageVO<>();
        DataVO<T> dataVO = new DataVO<>();

        dataVO.setRecords(records);
        dataVO.setRowCount(rowCount);
        dataVO.setPageSize(pageSize);
        dataVO.setTotalPage(totalPage);
        dataVO.setShowRealm(false); // 默认设为 false，可以根据实际需求调整

        pageVO.setData(dataVO);
        pageVO.setMessage("请求成功");
        pageVO.setMsgCode(HttpStatus.OK.value()); // 使用 200 表示成功

        return pageVO;
    }

    /**
     * 数据为 0 的响应方法，用于处理没有数据的分页响应
     *
     * @param pageSize 每页大小
     * @param <T>      数据类型
     * @return 数据为空的分页响应
     */
    public static <T> PageVO<T> empty(Long pageSize) {
        PageVO<T> pageVO = new PageVO<>();
        DataVO<T> dataVO = new DataVO<>();

        dataVO.setRecords(Collections.emptyList()); // 空数据
        dataVO.setRowCount(0L); // 总记录数为 0
        dataVO.setPageSize(pageSize);
        dataVO.setTotalPage(0L); // 总页数为 0
        dataVO.setShowRealm(false); // 默认设为 false，可以根据实际需求调整

        pageVO.setData(dataVO);
        pageVO.setMessage("请求成功");
        pageVO.setMsgCode(HttpStatus.OK.value());

        return pageVO;
    }

    /**
     * 分页查询结果转换方法
     *
     * 该方法用于将分页查询结果转换为统一的分页响应对象，支持自定义的映射逻辑。
     *
     * @param page 分页对象，包含查询到的分页数据
     * @param mapper 自定义的映射函数，将每个分页记录映射为目标对象
     * @return 包含分页数据的 PageVO 对象
     * @param <E> 查询结果中的实体类型
     * @param <V> 目标映射后的数据类型
     */
    public static <E, V> PageVO<V> convert(Page<E> page, Function<E, V> mapper) {
        List<V> records = page.getRecords().stream()
                .map(mapper)
                .toList();
        return PageVO.success(records, page.getTotal(), page.getPages(), page.getCurrent());
    }


}
