package com.github.fanzezhen.common.mp.model;

import com.github.fanzezhen.common.core.struct.AbstractBizLogStruct;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * @author zezhen.fan
 */
@Slf4j
public class SysUserBizLogStruct extends AbstractBizLogStruct {
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
