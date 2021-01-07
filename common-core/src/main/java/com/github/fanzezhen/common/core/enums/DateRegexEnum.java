package com.github.fanzezhen.common.core.enums;

import com.github.fanzezhen.common.core.constant.CommonConstant;
import com.github.fanzezhen.common.core.constant.RegexConstant;
import lombok.Getter;

/**
 * @author zezhen.fan
 */
public enum DateRegexEnum {
    /**
     * 连字符
     */
    HYPHEN(CommonConstant.DEFAULT_DATE_PATTERN, RegexConstant.HYPHEN_DATE),
    /**
     * 极简
     */
    MINIMALISM(CommonConstant.MINIMALISM_DATE_DATE_PATTERN, RegexConstant.MINIMALISM_DATE),
    ;
    @Getter
    private final String pattern;
    @Getter
    private final String regex;

    DateRegexEnum(String pattern, String regex) {
        this.pattern = pattern;
        this.regex = regex;
    }

}
