package com.github.fanzezhen.common.core.struct;

import com.github.fanzezhen.common.core.dict.SysUserDict;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class SysUserBizLogStruct extends BizLogStruct {
    static {
        serviceBeanName = "sysUserServiceImpl";
        beanMethodName = "getById";
        try {
            dict = SysUserDict.class.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            dict = new SysUserDict();
            log.error(e.getLocalizedMessage());
        }
    }
}
