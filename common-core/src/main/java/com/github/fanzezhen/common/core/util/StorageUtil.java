package com.github.fanzezhen.common.core.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 存储工具类
 *
 * @author fanzezhen
 */
@Slf4j
public class StorageUtil {

    static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)\\s*([a-zA-Z]+)");
    // 字节单位常量  
    private static final long BYTE = 1L;
    private static final long KILOBYTE = 1024L * BYTE;
    private static final long MEGABYTE = 1024L * KILOBYTE;
    private static final long GIGABYTE = 1024L * MEGABYTE;
    private static final long TERABYTE = 1024L * GIGABYTE;

    // 私有方法，用于根据单位返回乘数  
    private static long getMultiplier(String unit) {
        return switch (unit.toLowerCase()) {
            case "b", "byte", "bytes" -> BYTE;
            case "k", "kb", "kib" -> KILOBYTE;
            case "m", "mb", "mib" -> MEGABYTE;
            case "g", "gb", "gib" -> GIGABYTE;
            case "t", "tb", "tib" -> TERABYTE;
            default -> throw new IllegalArgumentException("Invalid unit: " + unit);
        };
    }

    // 将包含单位的字符串转换为字节数  
    public static Long convertToBytes(String sizeWithUnit) {
        // 使用正则表达式匹配数字和单位  
        Matcher matcher = NUMBER_PATTERN.matcher(sizeWithUnit);

        if (matcher.matches()) {
            // 提取数值和单位  
            double size = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(3).toLowerCase();

            // 计算字节数  
            return Math.round(size * getMultiplier(unit));
        } else {
            throw new IllegalArgumentException("Invalid size format: " + sizeWithUnit);
        }
    }

    public static String truncateStringForBytes(String input, int maxLengthInBytes, boolean isReverse) {
        if (input == null) {
            return null;
        }
        // 获取字符串的字节表示  
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        // 如果字符串已经小于或等于TEXT字段的最大长度，则无需截断  
        if (bytes.length <= maxLengthInBytes) {
            return input;
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] inputCharArray = input.toCharArray();
        if (isReverse) {
            for (int i = inputCharArray.length - (maxLengthInBytes / 4); i < inputCharArray.length; i++) {
                stringBuilder.append(inputCharArray[i]);
            }
            int byteLength = stringBuilder.toString().getBytes(StandardCharsets.UTF_8).length;
            for (int i = inputCharArray.length - (maxLengthInBytes / 4) - 1; i > 0; i--) {
                byteLength += String.valueOf(inputCharArray[i]).getBytes(StandardCharsets.UTF_8).length;
                if (byteLength > maxLengthInBytes) {
                    break;
                }
                stringBuilder.insert(0, inputCharArray[i]);
            }
        } else {
            for (int i = 0; i <= maxLengthInBytes / 4; i++) {
                stringBuilder.append(inputCharArray[i]);
            }
            int byteLength = stringBuilder.toString().getBytes(StandardCharsets.UTF_8).length;
            for (int i = maxLengthInBytes / 4 + 1; i < inputCharArray.length; i++) {
                byteLength += String.valueOf(inputCharArray[i]).getBytes(StandardCharsets.UTF_8).length;
                if (byteLength > maxLengthInBytes) {
                    break;
                }
                stringBuilder.append(inputCharArray[i]);
            }
        }
        return stringBuilder.toString();
    }

}
