package com.github.fanzezhen.common.core.util;

import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtil {
    private static short number = 0;

    private synchronized static short getNumber() {
        return number++;
    }

    public static Long createId() {
        return (long) Math.abs(UUID.randomUUID().toString().hashCode());
    }

    public static Long createIdViaUUID() {
        return (long) Math.abs(UUID.randomUUID().hashCode());
    }

    public static Long createIdViaTimeAndUUID() {
        String random = String.valueOf(Math.abs(UUID.randomUUID().hashCode()));
        return Long.valueOf(new Date().getTime() + random.substring(random.length() - 5));
    }

    public static Long createIdViaTimeAndNum() {
        return Long.valueOf(new Date().getTime() + String.format("%05d", Math.abs(getNumber())));
    }

    public static Date parseDateByStringAndPattern(String dateString, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateString);
    }

    /**
     * 将Object转换成List<?>，避免Unchecked cast: 'java.lang.Object' to 'java.util.List<?>'
     * objectToList(obj, String.class)
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
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
     * @param obj
     * @param clazz
     * @param <T>
     * @return
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

    public static HashMap<String, String> JsonToHashMap(JSONObject jsonObject){
        HashMap<String, String> data = new HashMap<>();
        for (String key:jsonObject.keySet()){
            String value = jsonObject.get(key).toString();
            data.put(key, value);
        }
        return data;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(createIdViaTimeAndNum());
        }
    }
}
