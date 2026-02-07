package com.oneself.common.infra.jdbc.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.oneself.common.core.exception.OneselfException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.ObjectUtils;

import java.util.function.Function;

/**
 * @author liuhuan
 * date 2025/1/2
 * packageName com.oneself.common.utils
 * className DuplicateCheckUtils
 * description 重复数据检查工具类
 * version 1.0
 */
@Slf4j
public class DuplicateCheckUtils {

    private DuplicateCheckUtils() {
        throw new AssertionError("此工具类不允许实例化");
    }

    /**
     * 条件类型枚举，表示字段查询使用的比较操作符。
     */
    public enum ConditionType {
        EQ,  // 等于
        NE,  // 不等于
        LIKE, // 模糊匹配
        GT,  // 大于
        LT,  // 小于
        GE,  // 大于等于
        LE   // 小于等于
    }

    /**
     * 通用唯一性校验（单字段），用于校验某字段值是否唯一。
     *
     * @param val          实体对象
     * @param getFieldFunc 被校验字段的 getter，例如 Entity::getName
     * @param getIdFunc    主键 ID 的 getter，例如 Entity::getId
     * @param countFunc    查询方法，返回满足条件的记录数，例如 wrapper -> mapper.selectCount(wrapper)
     * @param errorMessage 异常提示信息
     * @param <T>          实体类型
     * @param <ID>         主键类型
     */
    public static <T, ID> void checkDuplicate(
            T val,
            SFunction<T, ?> getFieldFunc,
            SFunction<T, ID> getIdFunc,
            Function<LambdaQueryWrapper<T>, Long> countFunc,
            String errorMessage
    ) {
        checkDuplicateMultiFields(
                val,
                getIdFunc,
                countFunc,
                errorMessage,
                FieldCondition.of(getFieldFunc)
        );
    }

    /**
     * 通用唯一性校验方法（支持组合字段和多种条件类型）。
     * 可用于任意字段组合唯一性校验，例如：部门名 + 父ID、用户名 LIKE 匹配等。
     *
     * @param val          实体对象，包含被校验的数据
     * @param getIdFunc    获取主键 ID 的 getter 方法引用
     * @param countFunc    查询方法，返回符合条件的记录数
     * @param errorMessage 校验失败时抛出的错误提示信息
     * @param conditions   字段条件列表（支持 eq, ne, like 等）
     * @param <T>          实体类型
     * @param <ID>         主键 ID 类型（如 Long、String）
     * @throws OneselfException 如果数据库中已存在重复数据则抛出异常
     */
    @SafeVarargs
    public static <T, ID> void checkDuplicateMultiFields(
            T val,
            SFunction<T, ID> getIdFunc,
            Function<LambdaQueryWrapper<T>, Long> countFunc,
            String errorMessage,
            FieldCondition<T>... conditions
    ) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        for (FieldCondition<T> condition : conditions) {
            Object value = condition.valueSupplier.apply(val);
            switch (condition.conditionType) {
                case EQ -> wrapper.eq(condition.fieldGetter, value);
                case NE -> wrapper.ne(condition.fieldGetter, value);
                case LIKE -> wrapper.like(condition.fieldGetter, value);
                case GT -> wrapper.gt(condition.fieldGetter, value);
                case GE -> wrapper.ge(condition.fieldGetter, value);
                case LT -> wrapper.lt(condition.fieldGetter, value);
                case LE -> wrapper.le(condition.fieldGetter, value);
                default -> throw new OneselfException("不支持的条件类型: " + condition.conditionType);
            }
        }

        ID id = getIdFunc.apply(val);
        if (ObjectUtils.isNotEmpty(id)) {
            wrapper.ne(getIdFunc, id);
        }

        if (countFunc.apply(wrapper) > 0) {
            throw new OneselfException(errorMessage);
        }
    }

    /**
     * 字段条件封装类，表示一个字段的查询条件（字段名、值、比较类型）。
     * 用于组合字段条件查询时传参，例如唯一性校验。
     *
     * @param <T> 实体类型
     */
    /**
     * 字段条件封装类，表示一个字段的查询条件（字段名、值、比较类型、是否生效判断）。
     * 用于组合字段条件查询时传参，例如唯一性校验。
     *
     * @param <T> 实体类型
     */
    public record FieldCondition<T>(
            SFunction<T, ?> fieldGetter,
            Function<T, ?> valueSupplier,
            ConditionType conditionType,
            Predicate<Object> effectiveChecker
    ) {

        /**
         * 全参数构造：指定字段、值获取方式、比较类型、有效性检查。
         */
        public static <T> FieldCondition<T> of(SFunction<T, ?> fieldGetter,
                                               Function<T, ?> valueSupplier,
                                               ConditionType conditionType,
                                               Predicate<Object> effectiveChecker) {
            return new FieldCondition<>(fieldGetter, valueSupplier, conditionType, effectiveChecker);
        }

        /**
         * 简化构造：默认有效性检查（null 或空字符串会被忽略）。
         */
        public static <T> FieldCondition<T> of(SFunction<T, ?> fieldGetter,
                                               Function<T, ?> valueSupplier,
                                               ConditionType conditionType) {
            return new FieldCondition<>(fieldGetter, valueSupplier, conditionType,
                    v -> !(v == null || (v instanceof String s && org.apache.commons.lang3.StringUtils.isBlank(s))));
        }

        /**
         * 简化构造：字段 getter 与值来源一致，默认 EQ 比较，默认有效性检查。
         */
        public static <T> FieldCondition<T> of(SFunction<T, ?> fieldGetter) {
            return of(fieldGetter, fieldGetter, ConditionType.EQ);
        }

        /**
         * 简化构造：字段 getter 与值来源一致，指定比较类型，默认有效性检查。
         */
        public static <T> FieldCondition<T> of(SFunction<T, ?> fieldGetter,
                                               ConditionType conditionType) {
            return of(fieldGetter, fieldGetter, conditionType);
        }
    }
}