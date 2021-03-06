package com.github.fanzezhen.common.core.constant;

/**
 * @author zezhen.fan
 */
public interface RegexConstant {
    /**
     * 手机号码正则表达式
     */
    String PHONE = "^[1][3-9][0-9]{9}$";
    /**
     * 日期正则表达式
     */
    String DEFAULT_DATE = "^\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}$";
    /**
     * （连接符-）日期正则表达式
     */
    String HYPHEN_DATE = "^\\d{4}(\\-)\\d{1,2}(\\-)\\d{1,2}$";
    /**
     * （无连接符）日期正则表达式
     */
    String MINIMALISM_DATE = "^\\d{4}\\d{2}\\d{2}$";
    /**
     * 手机号码正则表达式
     */
    String TIME = "\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}";
    /**
     * 手机号码正则表达式
     */
    String DATETIME = "^[1][3-9][0-9]{9}$";
}
