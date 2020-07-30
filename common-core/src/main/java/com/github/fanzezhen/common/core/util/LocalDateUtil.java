package com.github.fanzezhen.common.core.util;

import com.github.fanzezhen.common.core.constant.DateConstant;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class LocalDateUtil {

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDate 日期
     */
    public static String toDateString(LocalDate localDate) {
        return toDateString(localDate, DateConstant.DATE_DEFAULT_PATTERN);
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDate 日期
     * @param patterns  日期格式字符串
     */
    public static String toDateString(LocalDate localDate, String... patterns) {
        if (localDate == null) return "";
        for (String pattern : patterns) {
            try {
                return localDate.format(DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException dateTimeParseException) {
                log.info(dateTimeParseException.getLocalizedMessage() + pattern);
            }
        }
        log.warn("LocalDate转String失败：" + localDate);
        return null;
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDateTime 日期
     */
    public static String toDateString(LocalDateTime localDateTime) {
        return toDateTimeString(localDateTime, DateConstant.DATE_DEFAULT_PATTERN);
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDateTime 日期
     */
    public static String toDateTimeString(LocalDateTime localDateTime) {
        return toDateTimeString(localDateTime, DateConstant.DATE_TIME_DEFAULT_PATTERN);
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDateTime 日期
     * @param patterns      日期格式字符串
     */
    public static String toDateTimeString(LocalDateTime localDateTime, String... patterns) {
        if (localDateTime == null) return "";
        for (String pattern : patterns) {
            try {
                return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException dateTimeParseException) {
                log.info(dateTimeParseException.getLocalizedMessage() + pattern);
            }
        }
        log.warn("LocalDateTime转String失败：" + localDateTime);
        return null;
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param dateString 日期字符串
     */
    public static LocalDate toDate(String dateString) {
        return toDate(dateString, DateConstant.DATE_DEFAULT_PATTERN);
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param dateString 日期字符串
     * @param patterns   日期格式字符串
     */
    public static LocalDate toDate(String dateString, String... patterns) {
        for (String pattern : patterns) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateString, fmt);
            } catch (DateTimeParseException dateTimeParseException) {
                log.warn(dateTimeParseException.toString());
            }
        }
        return null;
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param dateTimeString 日期字符串
     */
    public static LocalDateTime toDateTime(String dateTimeString) {
        return toDateTime(dateTimeString, DateConstant.DATE_TIME_DEFAULT_PATTERN);
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param dateTimeString 日期字符串
     * @param patterns       日期格式字符串
     */
    public static LocalDateTime toDateTime(String dateTimeString, String... patterns) {
        for (String pattern : patterns) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(dateTimeString, fmt);
            } catch (DateTimeParseException dateTimeParseException) {
                log.warn(dateTimeParseException.toString());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate date = LocalDate.parse("2020-02-02 22:22:22", fmt);
        System.out.println(date);
    }
}
