package com.github.fanzezhen.common.core.constant;

/**
 * @author zezhen.fan
 */
@SuppressWarnings("unused")
public interface MpConstant {
    /**
     * 默认的包内文件夹名
     */
    String DEFAULT_DIR_JAVA = "/src/main/java";
    String DEFAULT_DIR_MAPPER = "/src/main/resources/mapper/";
    /**
     * 数据表中的逻辑删除字段
     */
    String DEFAULT_LOGIC_DELETE_COLUMN_NAME = "del_flag";
    String DEFAULT_FORMAT_FILE_NAME = "%sPo";
    /**
     * 数据表中的版本号字段
     */
    String VERSION_FIELD_NAME = "version";
    /**
     * 输入表名的默认提示语
     */
    String DEFAULT_TABLE_NAME_INPUT_HINT = "表名";
    String[] DEFAULT_IGNORE_TABLE_PREFIX = new String[]{"t_", "c_"};
}
