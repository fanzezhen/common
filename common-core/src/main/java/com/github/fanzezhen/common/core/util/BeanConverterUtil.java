package com.github.fanzezhen.common.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * @author zezhen.fan
 */
@Slf4j
public class BeanConverterUtil {

    /**
     * 将源POJO对象数据复制给目标POJO对象的同名属性
     *
     * @param source 源
     * @param target 目标
     * @param <S>    源数据类型
     * @param <T>    目标数据类型
     * @return 目标
     * @date 2018年12月4日
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
     * @param <S>源数据类型
     * @param <T>目标数据类型
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
     * @param <S>源数据类型
     * @param <T>目标数据类型
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
     * @param ss        源数据
     * @param cls       目标类型
     * @param <S>源数据类型
     * @param <T>目标数据类型
     * @return List
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
     * @param ss               源数据
     * @param cls              目标类型
     * @param ignoreProperties 忽略字段集合
     * @param <S>源数据类型
     * @param <T>目标数据类型
     * @return List
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
     * @param ss                   源数据
     * @param cls                  目标类型
     * @param ignoreNullProperties 忽略字段集合
     * @param <S>源数据类型
     * @param <T>目标数据类型
     * @return List
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
     * @param bean 对象
     * @return Map
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
     * @param map Map<String, Object>
     * @param bean bean
     * @return 对象
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将List<T>转换为List<Map<String, Object>>
     *
     * @param objList List<T>
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
     * @param maps List<Map<String, Object>>
     * @param clazz Class<T>
     * @return List<T>
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

    /**
     * 将Object转换成List<?>，避免Unchecked cast: 'java.lang.Object' to 'java.util.List<?>'
     * objectToList(obj, String.class)
     *
     * @param obj   obj
     * @param clazz clazz
     * @return List<T>
     */
    public static <T> List<T> objectToList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 将Object转换成Set<?>，避免Unchecked cast: 'java.lang.Object' to 'java.util.Set<?>'
     * objectToList(obj, String.class)
     *
     * @param obj   obj
     * @param clazz clazz
     * @return Set<T>
     */
    public static <T> Set<T> objectToSet(Object obj, Class<T> clazz) {
        Set<T> result = new HashSet<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    public static HashMap<String, String> jsonToHashMap(JSONObject jsonObject) {
        HashMap<String, String> data = new HashMap<>(10);
        for (String key : jsonObject.keySet()) {
            String value = jsonObject.get(key).toString();
            data.put(key, value);
        }
        return data;
    }
}
