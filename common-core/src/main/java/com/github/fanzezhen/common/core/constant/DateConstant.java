package com.github.fanzezhen.common.core.constant;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * @author zezhen.fan
 */
public class DateConstant {
    public static final char[] DATE_SPLITTERS = {'-', '/', ' ', '.', '+', '*', '|', '?'};
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_DEFAULT_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String[] DATE_PATTERNS = {"yyyy", "yyyy.", "yyyy年", "yyyyMM", "yyyy.MM", "yyyy-MM", "yyyy/MM",
            "yyyy年MM月", "yyyy-MM-dd", "yyyy年MM月dd日", "yyyyMMdd"};
    public static final String[] DATE_TIME_PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"};
    public static final String[] PATTERNS = ArrayUtils.addAll(DATE_PATTERNS, DATE_TIME_PATTERNS);

    public static void main(String[] args) {
        System.out.println(Arrays.toString(DateConstant.DATE_PATTERNS));
        System.out.println(Arrays.toString(DateConstant.DATE_TIME_PATTERNS));
        System.out.println(Arrays.toString(DateConstant.PATTERNS));
    }
}
