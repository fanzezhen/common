package com.github.fanzezhen.common.mp.base;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.common.mp.base.entity.BaseEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 基于mybatis-plus的基础dao层
 *
 * @author fanzezhen
 * @createTime 2024-05-25 11:29:51
 * @since 1.0.0
 */
public interface BaseMapper<T extends BaseEntity<?>> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    /**
     * 根据唯一字段查询
     */
    default T get(SFunction<T, ?> column, Object value) {
        LambdaQueryWrapper<T> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(column, value);
        return this.selectOne(lambdaQueryWrapper);
    }

    /**
     * 分页查询
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     */
    default Page<T> page(long currentPage, long pageSize, LambdaQueryWrapper<T> lambdaQueryWrapper) {
        return this.selectPage(new Page<>(currentPage, pageSize), lambdaQueryWrapper);
    }

    /**
     * 分页查询
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     */
    default Page<T> page(long currentPage, long pageSize) {
        return page(currentPage, pageSize, Wrappers.lambdaQuery());
    }

    /**
     * 查询全部
     */
    default List<T> list() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 查询列表
     *
     * @param pks 主键集合，当变量为null时返回全部，当变量为空集合时返回空列表，当变量有元素时按元素筛选
     */
    default List<T> list(Collection<? extends Serializable> pks) {
        if (CollUtil.isEmpty(pks)) {
            return Collections.emptyList();
        }
        return selectBatchIds(pks);
    }

    /**
     * 根据属性检查是否存在
     */
    default boolean existByColumn(SFunction<T, ?> column, Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return exists(wrapper);
    }

    /**
     * 根据属性检查是否存在
     */
    default boolean existByColumn(SFunction<T, ?> column, Collection<? extends Serializable> values) {
        if (CollUtil.isEmpty(values)) {
            return false;
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        wrapper.in(column, values);
        return exists(wrapper);
    }

    /**
     * 根据属性计数
     */
    default long countByColumn(SFunction<T, ?> column, Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return selectCount(wrapper);
    }

    /**
     * 根据属性计数
     */
    default long countByColumn(SFunction<T, ?> column, Collection<? extends Serializable> values) {
        if (CollUtil.isEmpty(values)) {
            return 0L;
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        wrapper.in(column, values);
        return selectCount(wrapper);
    }

    /**
     * 根据属性查询列表
     */
    default List<T> listByColumn(SFunction<T, ?> column, Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return selectList(wrapper);
    }

    /**
     * 根据属性查询列表
     */
    default List<T> listByColumn(SFunction<T, ?> column, Collection<? extends Serializable> values) {
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        wrapper.in(column, values);
        return selectList(wrapper);
    }

    /**
     * 根据属性查询列表
     */
    default List<T> listByColumn(SFunction<T, ?> column, Serializable value, Collection<? extends Serializable> pks) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (pks != null) {
            if (pks.isEmpty()) {
                return Collections.emptyList();
            }
            wrapper.in(T::getId, pks);
        }
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return selectList(wrapper);
    }

    /**
     * 根据属性查询列表
     */
    default List<T> listByColumn(SFunction<T, ?> column, Collection<? extends Serializable> values, Collection<? extends Serializable> pks) {
        if (CollUtil.isEmpty(values)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (pks != null) {
            if (pks.isEmpty()) {
                return Collections.emptyList();
            }
            wrapper.in(T::getId, pks);
        }
        wrapper.in(column, values);
        return selectList(wrapper);
    }

    /**
     * 批量保存
     */
    int insertBatchSomeColumn(List<T> relations);

    /**
     * 更新单个属性
     */
    default int updateColumn(SFunction<T, ?> column, Serializable value, Serializable id) {
        LambdaUpdateWrapper<T> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(column, value).eq(T::getId, id);
        return update(updateWrapper);
    }

    /**
     * 更新单个属性
     */
    default int updateColumn(SFunction<T, ?> column, Serializable value, Collection<? extends Serializable> pks) {
        LambdaUpdateWrapper<T> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(column, value).in(T::getId, pks);
        return update(updateWrapper);
    }

    /**
     * 根据属性删除
     */
    default int del(SFunction<T, ?> column, Serializable value) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (value == null) {
            wrapper.isNull(column);
        } else {
            wrapper.eq(column, value);
        }
        return delete(wrapper);
    }

    /**
     * 根据属性删除
     */
    default int del(SFunction<T, ?> column, Collection<? extends Serializable> values) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        if (values == null) {
            wrapper.isNull(column);
        } else {
            if (values.isEmpty()) {
                return 0;
            }
            wrapper.in(column, values);
        }
        return delete(wrapper);
    }

}
