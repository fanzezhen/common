package com.github.fanzezhen.common.core.enums.dict;

import com.github.fanzezhen.common.core.dict.AbstractDict;
import com.github.fanzezhen.common.core.dict.SysUserDict;

/**
 * @author zezhen.fan
 */
public enum DictEnum {
    /**
     * 系统用户字典
     */
    SYS_USER(SysUserDict.getInstance(), "系统用户字典");

    public final AbstractDict dict;
    public final String desc;


    DictEnum(AbstractDict dict, String desc) {
        this.dict = dict;
        this.desc = desc;
    }
}
