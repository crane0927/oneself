package com.oneself.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author liuhuan
 * date 2025/1/2
 * packageName com.oneself.common.utils
 * className DuplicateCheckUtils
 * description 重复数据检查工具类 TODO 逻辑未完成
 * version 1.0
 */
@Slf4j
public class DuplicateCheckUtils {

    /**
     * 检查实体对象中的字段值是否存在重复
     *
     * @param mapper      MyBatis-Plus 的 Mapper
     * @param entity      实体对象
     * @param uniqueField 唯一字段的名称
     * @param idField     ID 字段名称
     * @param idValue     当前记录的 ID 值
     */
    public static <T> void checkDuplicate(BaseMapper<T> mapper, T entity, String uniqueField, String idField, Object idValue) {
        try {
            // 创建 Lambda 查询包装器
            LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();

            // 排除当前记录：根据主键字段和主键值
            if (ObjectUtils.isNotEmpty(idField) && ObjectUtils.isNotEmpty(idValue)) {
                // 使用反射获取当前记录的主键字段值
                Method idGetter = entity.getClass().getMethod("get" + capitalizeFirstLetter(idField));
                Object currentIdValue = idGetter.invoke(entity);

                if (ObjectUtils.isNotEmpty(currentIdValue) && !currentIdValue.equals(idValue)) {
                    throw new RuntimeException("当前记录 ID 值与传入的 ID 值不一致！");
                }

                // 排除当前记录
                wrapper.ne(currentIdValue != null, entityField(entity, idField), idValue);
            }

            // 使用反射获取唯一字段的值
            Method uniqueFieldGetter = entity.getClass().getMethod("get" + capitalizeFirstLetter(uniqueField));
            Object fieldValue = uniqueFieldGetter.invoke(entity);

            // 生成查询条件
            wrapper.eq(ObjectUtils.isNotEmpty(fieldValue), entityField(entity, uniqueField), fieldValue);

            // 执行查询
            T existingEntity = mapper.selectOne(wrapper);

            if (ObjectUtils.isNotEmpty(existingEntity)) {
                String errorMessage = "已经存在名为【" + fieldValue + "】的数据，请重命名！";
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException("检查重复数据时出错：" + e.getMessage(), e);
        }
    }

    /**
     * 将字符串的首字母大写（用于反射时获取 getter 方法名）
     *
     * @param field 字段名
     * @return 首字母大写的字段名
     */
    private static String capitalizeFirstLetter(String field) {
        if (field == null || field.isEmpty()) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    /**
     * 获取字段对应的实体类 lambda 表达式
     *
     * @param entity 实体对象
     * @param field  字段名
     * @param <T>    实体类型
     * @return 字段对应的 lambda 表达式
     */
    private static <T> SFunction<T, ?> entityField(T entity, String field) {
        try {
            // 动态获取字段对应的 lambda 表达式
            return (SFunction<T, ?>) entity.getClass().getDeclaredField(field).get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("获取字段 Lambda 表达式失败：" + e.getMessage(), e);
        }
    }
}