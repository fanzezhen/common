package com.github.fanzezhen.common.core.constant;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * @author zezhen.fan
 */
public interface DateConstant {
    char[] DATE_SPLITTERS = {'-', '/', ' ', '.', '+', '*', '|', '?'};
    String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    String DATE_DEFAULT_PATTERN = "yyyy-MM-dd";
    String DATE_TIME_DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String[] DATE_PATTERNS = {"yyyy", "yyyy.", "yyyy年", "yyyyMM", "yyyy.MM", "yyyy-MM", "yyyy/MM",
            "yyyy年MM月", "yyyy-MM-dd", "yyyy年MM月dd日", "yyyyMMdd"};
    String[] DATE_TIME_PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"};
    String[] PATTERNS = ArrayUtils.addAll(DATE_PATTERNS, DATE_TIME_PATTERNS);
}
