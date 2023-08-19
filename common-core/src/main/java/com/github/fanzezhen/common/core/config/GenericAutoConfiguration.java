package com.github.fanzezhen.common.core.config;

import com.github.fanzezhen.common.core.thread.CommonThreadPoolExecutor;
import com.github.fanzezhen.common.core.thread.PoolExecutors;
import com.github.fanzezhen.common.core.property.CommonCoreProperties;
import com.github.fanzezhen.common.core.property.CommonThreadPoolProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zezhen.fan
 * @date 2023/8/14
 */
@Configuration
@EnableConfigurationProperties({CommonCoreProperties.class})
public class GenericAutoConfiguration {
    @Resource
    private CommonCoreProperties commonCoreProperties;

    @Bean
    @ConditionalOnMissingBean
    public CommonThreadPoolExecutor commonThreadPool() {
        CommonThreadPoolProperties threadPoolProperties = this.commonCoreProperties.getThreadPoolProperties();
        int coreSize = threadPoolProperties.getCoreSize();
        int maxSize = threadPoolProperties.getMaxSize();
        int queueCapacity = threadPoolProperties.getQueueCapacity();
        int keepAliveSeconds = threadPoolProperties.getKeepAliveSeconds();
        return PoolExecutors.newThreadPool("commonThreadPool", coreSize, maxSize, queueCapacity, keepAliveSeconds);
    }
}
