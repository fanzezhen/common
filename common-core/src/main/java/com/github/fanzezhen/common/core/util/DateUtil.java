package com.github.fanzezhen.common.core.util;

import com.github.fanzezhen.common.core.constant.DateConstant;
import com.github.fanzezhen.common.core.constant.RegexConstant;
import com.github.fanzezhen.common.core.enums.DateRegexEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zezhen.fan
 */
@Slf4j
public class DateUtil {
    /**
     * 输入字符串，返回日期
     *
     * @param dateString 日期字符串
     * @return 日期
     */
    public static Date toDate(String dateString) {
        return toDate(dateString, DateConstant.PATTERNS);
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param dateString 日期字符串
     * @param pattern    日期字符串格式
     * @return 日期
     */
    public static Date toDate(String dateString, String... pattern) {
        try {
            return DateUtils.parseDate(dateString, pattern);
        } catch (ParseException parseException) {
            log.info(parseException.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    public static boolean isDate(String dateString) {
        return isDate(dateString, DateConstant.PATTERNS);
    }

    public static boolean isDate(String dateString, String... pattern) {
        try {
            DateUtils.parseDate(dateString, pattern);
            return true;
        } catch (ParseException parseException) {
            log.info(parseException.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    public static String minimalismToDefault(String dateString, String defaultHyphen) {
        return dateString.substring(0, 4) + defaultHyphen + dateString.substring(4, 6) + defaultHyphen + dateString.substring(6);
    }

    public static String addTimeToDateString(String dateString, String supplement) {
        return DateUtil.isDate(dateString, RegexConstant.DEFAULT_DATE) ?
                (DateUtil.isDate(dateString, DateRegexEnum.MINIMALISM.getRegex()) ?
                        DateUtil.minimalismToDefault(dateString, "-") : dateString) + " " + supplement
                : dateString;
    }

    /**
     * 根据生日计算当前周岁数
     */
    public static int figureCurrentAge(Date birthday) {
        // 当前时间
        Calendar curr = Calendar.getInstance();
        // 生日
        Calendar born = Calendar.getInstance();
        born.setTime(birthday);
        return figureAge(born, curr);
    }

    /**
     * 根据生日计算当前周岁数
     */
    public static int figureCurrentAge(String birthday, String pattern) {
        Date birthDate = toDate(birthday, pattern);
        if (birthDate == null) {
            return -1;
        }
        // 当前时间
        Calendar curr = Calendar.getInstance();
        // 生日
        Calendar born = Calendar.getInstance();
        born.setTime(birthDate);
        return figureAge(born, curr);
    }

    public static int figureAge(Calendar born, Calendar targetCalendar) {
        // 年龄 = 目标年 - 出生年
        int age = targetCalendar.get(Calendar.YEAR) - born.get(Calendar.YEAR);
        if (age <= 0) {
            return 0;
        }
        // 如果当前月份小于出生月份: age-1
        // 如果当前月份等于出生月份, 且当前日小于出生日: age-1
        int currMonth = targetCalendar.get(Calendar.MONTH);
        int currDay = targetCalendar.get(Calendar.DAY_OF_MONTH);
        int bornMonth = born.get(Calendar.MONTH);
        int bornDay = born.get(Calendar.DAY_OF_MONTH);
        if (currMonth < bornMonth) {
            age--;
        } else if (currMonth == bornMonth && currDay <= bornDay) {
            age--;
        }

        return age;
    }

    /**
     * 日期分隔符，处理特殊字符
     *
     * @param splitter splitter
     * @return 处理后的字符串
     */
    private static String dealWithSplitter(char splitter) {
        switch (splitter) {
            case '.':
            case '+':
            case '*':
            case '|':
            case '?':
                return "[" + splitter + "]";
            case '^':
            case '(':
            case ')':
            case '{':
            case '}':
            case '[':
            case ']':
                return "\\" + splitter;
            default:
                return String.valueOf(splitter);
        }
    }

    /***
     * 判断字符串是否是yyyyMMdd、yyyyMdd、yyyyMMd、yyyyMd格式，分隔符为splitter参数
     * 例如：yyyy-MM-dd、yyyy.MM.dd
     *
     * @param mes mes
     * @param splitter splitter
     * @return boolean
     */
    public static boolean isDateFormat(String mes, char splitter) {
        if (null == mes || 0 == mes.length()) {
            return false;
        }
        String strSplitter = dealWithSplitter(splitter);
        // 1000年——2020
//        String format = "(1[0-9]{3}|20([0-1]{1}[0-9]{1}|20))" + strSplitter + "([1-9]|0[1-9]|1[012])" + strSplitter + "([1-9]|0[1-9]|[12][0-9]|3[01])";
        // 0000年——9999年
        String format = "([0-9]{4})" + strSplitter + "([1-9]|0[1-9]|1[012])" + strSplitter + "([1-9]|0[1-9]|[12][0-9]|3[01])";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(mes);
        if (!matcher.matches()) {
            return false;
        }
        pattern = Pattern.compile("(\\d{4})" + strSplitter + "(\\d{1,2})" + strSplitter + "(\\d{1,2})");
        matcher = pattern.matcher(mes);
        if (!matcher.matches()) {
            return false;
        }
        int y = Integer.parseInt(matcher.group(1));
        int m = Integer.parseInt(matcher.group(2));
        int d = Integer.parseInt(matcher.group(3));
        int februaryDays = 28;
        if (d > februaryDays) {
            Calendar c = Calendar.getInstance();
            c.set(y, m - 1, 1);
            int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            return lastDay >= d;
        }
        return true;
    }

    /***
     * 判断字符串是否是yyyyMMdd、yyyyMdd、yyyyMMd、yyyyMd格式
     *
     * @param mes mes
     * @return boolean
     */
    public static boolean isDateFormat(String mes) {
        for (char splitter : DateConstant.DATE_SPLITTERS) {
            if (isDateFormat(mes, splitter)) {
                return true;
            }
        }
        return false;
    }
}
