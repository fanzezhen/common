package com.github.fanzezhen.common.core.thread;

import com.github.fanzezhen.common.core.property.CommonCoreProperties;
import com.github.fanzezhen.common.core.service.AbstractAnnotationFieldService;
import com.github.fanzezhen.common.core.strategy.AnnotationFieldServiceStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author zezhen.fan
 * @date 2023/10/13
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
