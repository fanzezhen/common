package com.github.fanzezhen.common.leaned;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import java.util.*;

/**
 * @author fanzezhen
 */
public class ColumnBasedStorage {
    public static void main(String[] args) {
        doSendData();
    }

    /**
     * 业务系统发送给中转平台
     */
    private static void doSendData() {
        //language=JSON， 业务系统发送给中转平台的数据
        String jsonData1 = "{\"biz_id\": \"1\", \"name\": \"受试者①号\", \"sex\": 1, \"multipleField\": \"a,b\"}";
        String jsonData2 = "{\"biz_id\": \"2\", \"name\": \"受试者②号\", \"sex\": 0, \"multipleField\": \"b,c\"}";
        List<String> jsonDataList = Arrays.asList(jsonData1, jsonData2);
        // 保留字段
        LinkedHashSet<String> holdFieldSet = new LinkedHashSet<>() {{
            add("biz_id");
        }};
        // 数据字典
        HashMap<String, TreeMap<Object, Object>> dict = new HashMap<>(2);
        dict.put("sex", new TreeMap<>(Map.of(0, "女", 1, "男", 2, "未知")));
        dict.put("multipleField", new TreeMap<>(Map.of("a", "字母A", "b", "字母B", "c", "字母C")));
        // 多选字段配置（key为字段名， value为分隔符）
        HashMap<String, String> multipleConfig = new HashMap<>(1);
        multipleConfig.put("multipleField", ",");

        dataMidPlatformRec(jsonDataList, holdFieldSet, dict, multipleConfig);
    }

    /**
     * 数据中转平台接受数据并执行标准化
     *
     * @param jsons          数据列表
     * @param holdFieldSet   需要保留的字段集合
     * @param dict           字典
     * @param multipleConfig 多选字段相关配置
     */
    private static void dataMidPlatformRec(Collection<String> jsons,
                                           LinkedHashSet<String> holdFieldSet,
                                           HashMap<String, TreeMap<Object, Object>> dict,
                                           HashMap<String, String> multipleConfig) {
        jsons.forEach(jsonData -> {
            JSONObject jsonObject = JSON.parseObject(jsonData, JSONObject.class, Feature.OrderedField);
            // 标准化的数据列表
            List<LinkedHashMap<String, Object>> standardizationList = new ArrayList<>();
            jsonObject.keySet().stream().filter(key -> !holdFieldSet.contains(key)).forEach(fieldName -> {
                // 标准化
                Object value = jsonObject.get(fieldName);
                List<Object> valueList = new ArrayList<>();
                List<Object> checkedValueList = new ArrayList<>();
                // 是否存在字典值映射
                if (dict.containsKey(fieldName)) {
                    TreeMap<Object, Object> fieldDict = dict.get(fieldName);
                    valueList.addAll(fieldDict.values());
                    // 是否是多选字段
                    if (multipleConfig.containsKey(fieldName)) {
                        String[] values = String.valueOf(value).split(multipleConfig.get(fieldName));
                        Arrays.stream(values).forEach(v -> checkedValueList.add(fieldDict.get(v)));
                    } else {
                        checkedValueList.add(fieldDict.get(value));
                    }
                } else {
                    valueList.add(value);
                    checkedValueList.add(value);
                }
                valueList.forEach(v -> {
                    // 标准化的数据
                    LinkedHashMap<String, Object> standardization = new LinkedHashMap<>();
                    standardization.put("id", UUID.randomUUID());
                    holdFieldSet.forEach(holdFieldName -> standardization.put(holdFieldName, jsonObject.get(holdFieldName)));
                    standardization.put("field", fieldName);
                    standardization.put("value", v);
                    standardization.put("checked", checkedValueList.contains(v));
                    standardizationList.add(standardization);
                });
            });
            standardizationList.forEach(System.out::println);
        });
    }
}
