package com.github.fanzezhen.common.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class BeanConverterUtil {

    /**
     * 将源POJO对象数据复制给目标POJO对象的同名属性
     *
     * @param source 源
     * @param target 目标
     * @param <S>
     * @param <T>
     * @return 目标
     * @date 2018年12月4日
     * @version 1.0
     */
    public static <S, T> T copy(S source, T target) {
        BeanUtil.copyProperties(source, target);
        return target;
    }

    /**
     * 将源POJO对象数据复制给目标POJO对象的同名属性
     *
     * @param source           源
     * @param target           目标
     * @param ignoreProperties 无需转换的属性
     * @param <S>
     * @param <T>
     * @return 目标
     * @date 2019-1-29
     */
    public static <S, T> T copy(S source, T target, String... ignoreProperties) {
        BeanUtil.copyProperties(source, target, ignoreProperties);
        return target;
    }


    /**
     * 将源POJO对象数据复制给目标POJO对象的同名属性
     *
     * @param source               源
     * @param target               目标
     * @param ignoreNullProperties 是否无视null值字段（如果为true则无视）
     * @param <S>
     * @param <T>
     * @return 目标
     * @date 2019-1-29
     */
    public static <S, T> T copy(S source, T target, boolean ignoreNullProperties) {
        BeanUtil.copyProperties(source, target, CopyOptions.create().setIgnoreNullValue(ignoreNullProperties));
        return target;
    }

    /**
     * 对象List复制
     *
     * @param ss
     * @param cls
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyList(List<S> ss, Class<T> cls) {

        List<T> tRes = new ArrayList<>();
        try {
            for (S s : ss) {
                T t = cls.getDeclaredConstructor().newInstance();
                BeanUtil.copyProperties(s, t);
                tRes.add(t);
            }
        } catch (Exception e) {
            log.info("类型转换异常，异常信息: ", e);
        }

        return tRes;
    }


    /**
     * 对象List复制
     *
     * @param ss
     * @param cls
     * @param ignoreProperties
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyList(List<S> ss, Class<T> cls, String... ignoreProperties) {

        List<T> tRes = new ArrayList<>();
        try {
            for (S s : ss) {
                T t = cls.getDeclaredConstructor().newInstance();
                BeanUtil.copyProperties(s, t, ignoreProperties);
                tRes.add(t);
            }
        } catch (Exception e) {
            log.info("类型转换异常，异常信息: ", e);
        }

        return tRes;
    }

    /**
     * 对象List复制
     *
     * @param ss
     * @param cls
     * @param ignoreNullProperties
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyList(List<S> ss, Class<T> cls, boolean ignoreNullProperties) {

        List<T> tRes = new ArrayList<>();
        try {
            for (S s : ss) {
                T t = cls.getDeclaredConstructor().newInstance();
                BeanUtil.copyProperties(s, t, CopyOptions.create().setIgnoreNullValue(ignoreNullProperties));
                tRes.add(t);
            }
        } catch (Exception e) {
            log.info("类型转换异常，异常信息: ", e);
        }

        return tRes;
    }

    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(String.valueOf(key), beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将List<T>转换为List<Map<String, Object>>
     *
     * @param objList
     */
    public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (objList != null && objList.size() > 0) {
            Map<String, Object> map;
            T bean;
            for (T t : objList) {
                bean = t;
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 将List<Map<String,Object>>转换为List<T>
     *
     * @param maps
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps, Class<T> clazz)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<T> list = Lists.newArrayList();
        if (maps != null && maps.size() > 0) {
            Map<String, Object> map;
            T bean;
            for (Map<String, Object> stringObjectMap : maps) {
                map = stringObjectMap;
                bean = clazz.getDeclaredConstructor().newInstance();
                mapToBean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }

}
