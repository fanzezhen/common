package com.github.fanzezhen.common.core.util;

import org.springframework.util.StringUtils;

import java.util.Date;

public class CastUtil {

    public static Object cast(Class<?> classType, Object value) {
        if (StringUtils.isEmpty(value)) return classType == String.class ? value : null;
        if (value instanceof String) {
            String valueString = String.valueOf(value);
            if (classType == Date.class)
                value = DateUtil.toDate(valueString);
            else if (classType == Long.class)
                value = Long.valueOf(valueString);
        }
        return value;
    }
}
