package com.github.fanzezhen.common.core.enums.db;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author zezhen.fan
 */
public enum DelFlagEnum implements IEnum<Integer> {
    /**
     * 未删除
     */
    NotDeleted(0, "未删除"),
    /**
     * 已删除
     */
    deleted(1, "已删除");

    @EnumValue
    @JsonValue
    public final int value;
    public final String desc;

    @Override
    public Integer getValue() {
        return this.value;
    }

    DelFlagEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
