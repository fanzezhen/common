package com.github.fanzezhen.common.core.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zezhen.fan
 */
@Slf4j
public class CommonUtil {
    public static void doClose(Closeable... closeAbles) {
        for (Closeable closeable : closeAbles) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    log.warn(e.getLocalizedMessage());
                }
            }
        }
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
