package com.github.fanzezhen.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author zezhen.fan
 */
@Slf4j
public class CastUtil {

    public static <T> T cast(Class<T> classType, Object value) {
        try {
            T result = classType.getDeclaredConstructor().newInstance();
            if (value == null) {
                return result;
            }
            if (value instanceof String) {
                String valueString = String.valueOf(value);
                if (classType == Date.class) {
                    result = (T) DateUtil.toDate(valueString);
                } else if (classType == Long.class) {
                    result = (T) Long.valueOf(valueString);
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public static Map<String, String> toMap(Object obj) {
        Map<String, String> returnMap = new HashMap<>(10);
        if (obj instanceof Map) {
            Set<Map.Entry> entrySet = ((Map) obj).entrySet();
            for (Map.Entry entry : entrySet) {
                Object mapKey = entry.getKey();
                Object mapValue = entry.getValue();
                returnMap.put(String.valueOf(mapKey), String.valueOf(mapValue));
            }
        } else {
            try {
                returnMap = BeanUtils.describe(obj);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.warn(e.getLocalizedMessage());
            }
        }
        return returnMap;
    }
}
