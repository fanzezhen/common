package com.github.fanzezhen.common.core.enums.db.log;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 登录日志类型枚举
 * @author zezhen.fan
 */
public enum OperationLogTypeEnum {
    /**
     * 新增
     */
    ADD(1, "新增"),
    /**
     * 修改
     */
    UPDATE(2, "修改");

    @EnumValue
    @JsonValue
    public final int type;
    @Getter
    private final String desc;

    OperationLogTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean needCompare(int type) {
        return type != ADD.type;
    }
}
