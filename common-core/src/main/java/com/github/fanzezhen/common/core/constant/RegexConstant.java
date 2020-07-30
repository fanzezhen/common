package com.github.fanzezhen.common.core.constant;

public class RegexConstant {
    public static final String PHONE = "^[1][3-9][0-9]{9}$";  //手机号码正则表达式
    public static final String DEFAULT_DATE = "^\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}$";  // 日期正则表达式
    public static final String HYPHEN_DATE = "^\\d{4}(\\-)\\d{1,2}(\\-)\\d{1,2}$";  // （连接符-）日期正则表达式
    public static final String MINIMALISM_DATE = "^\\d{4}\\d{2}\\d{2}$";  // （无连接符）日期正则表达式
    public static final String TIME = "\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}";  // 手机号码正则表达式
    public static final String DATETIME = "^[1][3-9][0-9]{9}$";  // 手机号码正则表达式
}
