package com.github.fanzezhen.common.core.util;

import org.springframework.util.StringUtils;

public class StringUtil {
    public static String dealNull(String origin, String target){
        if (StringUtils.isEmpty(origin)) return target;
        else return origin;
    }
    public static String dealNull(String origin){
        return dealNull(origin, "");
    }
}
