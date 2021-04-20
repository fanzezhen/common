package com.github.fanzezhen.common.core.enums.db.log;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 登录日志类型枚举
 * @author zezhen.fan
 */
public enum ExceptionTypeEnum {
    /**
     * 登录成功
     */
    DEFAULT(0, "默认");

    @EnumValue
    @JsonValue
    public final int type;
    @Getter
    private final String desc;

    ExceptionTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
