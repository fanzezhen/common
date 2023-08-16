package com.github.fanzezhen.common.core.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.github.fanzezhen.common.core.context.SysContext;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;
import java.util.List;

/**
 * @author zezhen.fan
 * @date 2023/8/16
 */
@Configuration
@ConditionalOnClass(
        name = {"org.springframework.cloud.openfeign.EnableFeignClients"}
)
public class OpenFeignClientConfiguration {
    private static final Logger log = LoggerFactory.getLogger(OpenFeignClientConfiguration.class);

    public OpenFeignClientConfiguration() {
    }

    @Bean
    public RequestInterceptor systemContextInterceptor() {
        return (requestTemplate) -> {
            SysContext systemContext = SysContextHolder.getSysContext();
            if (systemContext != null) {
                List<Pair<String, String>> pairs = systemContext.toHeaders();
                Pair<String, String> pair;
                if (CollUtil.isNotEmpty(pairs)) {
                    for (Iterator<Pair<String, String>> var3 = pairs.iterator(); var3.hasNext(); requestTemplate.header(pair.getKey(), pair.getValue())) {
                        pair = var3.next();
                        if (log.isDebugEnabled()) {
                            log.debug("add header:{},value:{} to feign request", pair.getKey(), pair.getValue());
                        }
                    }
                }
            }
        };
    }
}
