package com.github.fanzezhen.common.core.util;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.constant.DateConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

/**
 * @author zezhen.fan
 */
@Slf4j
public class LocalDateUtil {

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDate 日期
     */
    public static String toDateString(ChronoLocalDate localDate) {
        return toDateString(localDate, DateConstant.DATE_DEFAULT_PATTERN);
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDate 日期
     * @param patterns  日期格式字符串
     */
    public static String toDateString(ChronoLocalDate localDate, String... patterns) {
        if (localDate == null) {
            return "";
        }
        for (String pattern : patterns) {
            try {
                return localDate.format(getDateTimeFormatter(pattern));
            } catch (DateTimeParseException dateTimeParseException) {
                log.info(dateTimeParseException.getLocalizedMessage() + pattern);
            }
        }
        log.warn("toDateString失败：" + localDate);
        return null;
    }

    /**
     * 输入日期和分隔符，返回日期
     *
     * @param localDate  日期
     * @param separators 分隔符
     */
    public static String toDateString(LocalDate localDate, char... separators) {
        if (localDate == null || separators == null) {
            return "";
        }
        switch (separators.length) {
            case 1:
                String separator = String.valueOf(separators[0]);
                return localDate.getYear() + separator + localDate.getMonthValue() + separator + localDate.getDayOfMonth();
            case 2:
                return "" + localDate.getYear() + separators[0] + localDate.getMonthValue() + separators[1];
            case 3:
                return "" + localDate.getYear() + separators[0] + localDate.getMonthValue() + separators[1] +
                        localDate.getDayOfMonth() + separators[2];
            default:
        }
        log.warn("toDateString({})失败：" + localDate, separators);
        return "";
    }

    /**
     * 输入时间和分隔符，返回日期
     *
     * @param localDateTime 时间
     * @param separators    分隔符
     */
    public static String toDateString(LocalDateTime localDateTime, char... separators) {
        String dateString = StrUtil.EMPTY;
        if (localDateTime == null || separators == null) {
            return dateString;
        }
        switch (separators.length) {
            case 1:
                String separator = String.valueOf(separators[0]);
                dateString = localDateTime.getYear() + separator + localDateTime.getMonthValue() +
                        separator + localDateTime.getDayOfMonth();
                break;
            case 2:
                dateString = "" + localDateTime.getYear() + separators[0] + localDateTime.getMonthValue() + separators[1];
                break;
            case 3:
                dateString = "" + localDateTime.getYear() + separators[0] + localDateTime.getMonthValue() +
                        separators[1] + localDateTime.getDayOfMonth() + separators[2];
                break;
            default:
        }
        log.warn("toDateString({})失败：" + localDateTime, separators);
        return dateString;
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param localDateTime 时间
     * @param patterns      日期格式字符串
     */
    public static String toDateTimeString(LocalDateTime localDateTime, String... patterns) {
        if (localDateTime == null) {
            return "";
        }
        for (String pattern : patterns) {
            try {
                return localDateTime.format(getDateTimeFormatter(pattern));
            } catch (DateTimeParseException dateTimeParseException) {
                log.info(dateTimeParseException.getLocalizedMessage() + pattern);
            }
        }
        log.warn("toDateTimeString失败：" + localDateTime);
        return null;
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
     * @param dateString 日期字符串
     */
    public static LocalDate toDate(String dateString) {
        return toDate(dateString, DateConstant.PATTERNS);
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
                return LocalDate.parse(dateString, getDateTimeFormatter(pattern));
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
        LocalDateTime localDateTime = toDateTime(dateTimeString, DateConstant.DATE_TIME_PATTERNS);
        if (localDateTime == null) {
            LocalDate localDate = toDate(dateTimeString, DateConstant.DATE_PATTERNS);
            if (localDate != null) {
                localDateTime = localDate.atStartOfDay();
            }
        }
        return localDateTime;
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
                return LocalDateTime.parse(dateTimeString, getDateTimeFormatter(pattern));
            } catch (DateTimeParseException dateTimeParseException) {
                log.warn(dateTimeParseException.toString());
            }
        }
        return null;
    }

    /**
     * 输入字符串和模板字符串，返回日期
     *
     * @param startDateInclusiveString 日期字符串
     * @param endDateExclusiveString   日期字符串
     * @param patterns                 日期格式字符串
     */
    public static Period period(String startDateInclusiveString, String endDateExclusiveString, String... patterns) {
        for (String pattern : patterns) {
            try {
                LocalDate startDateInclusive = LocalDate.parse(startDateInclusiveString, getDateTimeFormatter(pattern));
                LocalDate endDateExclusive = LocalDate.parse(endDateExclusiveString, getDateTimeFormatter(pattern));
                return Period.between(startDateInclusive, endDateExclusive);
            } catch (DateTimeParseException dateTimeParseException) {
                log.warn(dateTimeParseException.getLocalizedMessage());
            }
        }
        return Period.ZERO;
    }

    /**
     * 输入字符串，返回日期间隔
     *
     * @param startDateInclusiveString 日期字符串
     * @param endDateExclusiveString   日期字符串
     */
    public static Period period(String startDateInclusiveString, String endDateExclusiveString) {
        if (StringUtils.isBlank(startDateInclusiveString) || StringUtils.isBlank(endDateExclusiveString)) {
            return Period.ZERO;
        }
        return period(startDateInclusiveString, endDateExclusiveString, DateConstant.DATE_PATTERNS);
    }

    private static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
    }

    public static void main(String[] args) {
        Period p = period("2016-01-01", "2002-01-01");
        System.out.println(p.getYears());
        System.out.println(p.getMonths());
    }
}
