package com.github.fanzezhen.common.core.thread;

import com.github.fanzezhen.common.core.property.CommonCoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zezhen.fan
 */
@Component
public class ThreadPoolPostConstruct {
    private final CommonCoreProperties commonCoreProperties;

    @Autowired
    public ThreadPoolPostConstruct(CommonCoreProperties commonCoreProperties) {
        this.commonCoreProperties = commonCoreProperties;
    }

    @PostConstruct
    public void init() {
        PoolExecutors.threadPoolProperties = commonCoreProperties.getThreadPoolProperties();
    }
}
