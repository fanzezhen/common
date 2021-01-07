package com.github.fanzezhen.common.core.enums.db;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zezhen.fan
 */
public enum SexEnum {
    /**
     * 女
     */
    WOMAN(0, "女"),
    /**
     * 男
     */
    MAN(1, "男"),
    /**
     * 未知
     */
    UNKNOWN(2, "未知"),
    /**
     * 未说明
     */
    UNSPECIFIED(3, "未说明");

    @EnumValue
    @JsonValue
    public final int code;
    @Getter
    private final String desc;

    SexEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        if (code != null) {
            SexEnum[] var1 = values();
            for (SexEnum enumItem : var1) {
                if (code.equals(enumItem.code)) {
                    return enumItem.getDesc();
                }
            }
            return String.valueOf(code);
        }
        return StrUtil.EMPTY;
    }

    public static SexEnum toEnum(int code) {
        for (SexEnum statusEnum : values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

}
