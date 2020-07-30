package com.github.fanzezhen.common.core.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class NumberUtils {
    /**
     * 阿拉伯数字转中文数字
     * @param string
     * @return
     */
    public static String numToChinese(String string) {
        String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] s2 = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        StringBuilder result = new StringBuilder();
        int n = string.length();
        for (int i = 0; i < n; i++) {
            int num = string.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result.append(s1[num]).append(s2[n - 2 - i]);
            } else {
                result.append(s1[num]);
            }
        }
        if (result.indexOf("一十零") == 0) {
            return result.substring(1, 2);
        } else if (result.indexOf("一十") == 0) {
            return result.substring(1);
        }
        return result.toString();
    }

    /**
     * 中文數字转阿拉伯数组【十万九千零六十  --> 109060】
     *
     * @param chineseNumber
     * @return
     */
    @SuppressWarnings("unused")
    public static Integer chineseNumberToInteger(String chineseNumber) {
        if (StringUtils.isEmpty(chineseNumber)) return null;
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九'};
        char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if (0 != count) {//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if (b) {//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return str != null && pattern.matcher(str).matches();
    }

    /**
     * 判断是否为数字格式不限制位数
     * @param o
     *     待校验参数
     * @return
     *     如果全为数字，返回true；否则，返回false
     */
    public static boolean isNumber(Object o){
        if (StringUtils.isEmpty(o)) return false;
        return  (Pattern.compile("[0-9]*")).matcher(String.valueOf(o)).matches();
    }

    /**
     * 判断是否为数值格式不限制位数
     * @param o
     *     待校验参数
     * @return
     *     如果是整数或小数，返回true；否则，返回false
     */
    public static boolean isNumeric(Object o){
        if (StringUtils.isEmpty(o)) return false;
        Pattern pattern = Pattern.compile("^([-+])?\\d+(\\.\\d+)?$");
        return pattern.matcher((CharSequence) o).matches();
    }
}
