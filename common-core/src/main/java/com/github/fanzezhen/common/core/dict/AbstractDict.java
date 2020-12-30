package com.github.fanzezhen.common.core.dict;

import lombok.Data;

import java.util.LinkedHashMap;

/**
 * 字典映射抽象字典类
 *
 * @author fengshuonan
 */
@Data
public abstract class AbstractDict {
    public static final String instanceMethodName = "getInstance";
    /**
     * 字典映射，实体的字段名→字段标题
     */
    protected LinkedHashMap<String, String> dict = new LinkedHashMap<>();
    /**
     * 字典映射，实体标题→实体的字段名
     */
    protected LinkedHashMap<String, String> reverseDict = new LinkedHashMap<>();
    /**
     * 字段枚举值映射，实体的字段名→{枚举名：枚举值}
     */
    protected LinkedHashMap<String, LinkedHashMap<Object, String>> fieldWrapperDict = new LinkedHashMap<>();
    /**
     * 字段枚举值映射，实体的字段名→{枚举值：枚举名}
     */
    protected LinkedHashMap<String, LinkedHashMap<String, Object>> fieldAdapterDict = new LinkedHashMap<>();

    public AbstractDict() {
        put("id", "主键id");
        put("name", "名称");

        putReverse("ID", "id");
        putReverse("名称", "name");

        init();
        initReverse();
        initBeWrapped();
        initBeAdapter();
    }

    /**
     * 初始化字段英文名称和中文名称对应的字典
     *
     * @author stylefeng
     */
    public abstract void init();

    /**
     * 初始化字段中文名称和英文名称对应的字典
     *
     * @author stylefeng
     */
    protected abstract void initReverse();

    /**
     * 初始化需要被包装的字段(例如:性别为1:男,0:女,需要被包装为汉字)
     *
     * @author stylefeng
     */
    protected abstract void initBeWrapped();

    /**
     * 初始化需要被解包装的字段(例如:性别为男:1,女:0,需要被解包装为代码)
     *
     * @author stylefeng
     */
    protected abstract void initBeAdapter();

    public boolean containsKey(Object key) {
        return dict.containsKey(String.valueOf(key));
    }

    public boolean containsReverseKey(Object key) {
        return reverseDict.containsKey(String.valueOf(key));
    }

    public String get(String key) {
        if (this.dict.get(key) == null) {
            return key;
        }
        return this.dict.get(key);
    }

    public void put(String key, String value) {
        this.dict.put(key, value);
    }

    public String getReverse(String key) {
        if (this.reverseDict.get(key) == null) {
            return key;
        }
        return this.reverseDict.get(key);
    }

    public void putReverse(String key, String value) {
        this.reverseDict.put(key, value);
    }

    public LinkedHashMap<Object, String> getFieldWrapper(String key) {
        return this.fieldWrapperDict.get(key);
    }

    public void putFieldWrapper(String key, LinkedHashMap<Object, String> fieldWrapper) {
        this.fieldWrapperDict.put(key, fieldWrapper);
    }

    public String getFieldWrapper(String field, Object value) {
        try {
            return this.fieldWrapperDict.get(field).get(value);
        } catch (NullPointerException nullPointerException) {
            return String.valueOf(value);
        }
    }

    public void putFieldWrapper(String field, Object value, String desc) {
        if (this.fieldWrapperDict.containsKey(field)) {
            getFieldWrapper(field).put(value, desc);
        } else {
            putFieldWrapper(field, new LinkedHashMap<>(1) {{
                put(value, desc);
            }});
        }
    }

    public LinkedHashMap<String, Object> getFieldAdapter(String key) {
        return this.fieldAdapterDict.get(key);
    }

    public void putFieldAdapter(String key, LinkedHashMap<String, Object> fieldAdapter) {
        this.fieldAdapterDict.put(key, fieldAdapter);
    }

    public Object getFieldAdapter(String field, String value) {
        try {
            return this.fieldAdapterDict.get(field).get(value);
        } catch (NullPointerException nullPointerException) {
            return value;
        }
    }

    public void putFieldAdapter(String field, String value, Object desc) {
        this.fieldAdapterDict.get(field).put(value, desc);
    }

    public Object getFieldAdapterByReverse(String reverseKey, String value) {
        return getFieldAdapter(getReverse(reverseKey), value);
    }

    private static class SingletonHolder {
        public static AbstractDict INSTANCE;
    }

    public static AbstractDict getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
