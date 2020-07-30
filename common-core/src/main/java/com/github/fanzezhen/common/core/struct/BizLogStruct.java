package com.github.fanzezhen.common.core.struct;


import com.github.fanzezhen.common.core.dict.AbstractDict;

public abstract class BizLogStruct {
    protected static String serviceBeanName;
    protected static String beanMethodName = "getById";
    protected static AbstractDict dict;

    public String getServiceBeanName() {
        return serviceBeanName;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    public AbstractDict getDict() {
        return dict;
    }
}
