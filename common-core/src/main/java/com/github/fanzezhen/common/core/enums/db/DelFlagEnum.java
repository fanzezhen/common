package com.github.fanzezhen.common.core.enums.db;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zezhen.fan
 */
public enum DelFlagEnum implements IEnum<Integer> {
    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除"),
    /**
     * 已删除
     */
    DELETED(1, "已删除");

    @EnumValue
    @JsonValue
    public final int code;
    @Getter
    private final String desc;

    @Override
    public Integer getValue() {
        return this.code;
    }

    DelFlagEnum(int value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        if (code != null) {
            DelFlagEnum[] var1 = values();
            for (DelFlagEnum enumItem : var1) {
                if (code.equals(enumItem.code)) {
                    return enumItem.getDesc();
                }
            }
            return String.valueOf(code);
        }
        return StrUtil.EMPTY;
    }

    public static DelFlagEnum toEnum(int code) {
        for (DelFlagEnum enumItem : values()) {
            if (enumItem.code == code) {
                return enumItem;
            }
        }
        return null;
    }

    public static String getColumn() {
        return "del_flag";
    }
}
