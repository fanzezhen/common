/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fanzezhen.common.gateway.core.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * @author zezhen.fan
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.gateway.enabled", matchIfMissing = true)
@AutoConfigureBefore(HttpHandlerAutoConfiguration.class)
@AutoConfigureAfter({MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class})
@ConditionalOnClass({DispatcherHandler.class, MeterRegistry.class, MetricsAutoConfiguration.class})
public class GatewayApiMetricsAutoConfiguration {

    @Bean
    @DependsOn("meterRegistry")
    @ConditionalOnBean(MeterRegistry.class)
    public GatewayApiMetricsFilter gatewayApiMetricFilter(MeterRegistry meterRegistry) {
        return new GatewayApiMetricsFilter(meterRegistry);
    }

    @Bean
    @DependsOn("inetUtils")
    @ConditionalOnBean(InetUtils.class)
    public DefaultTagMeterFilter defaultTagMeterFilter(InetUtils inetUtils) {
        return new DefaultTagMeterFilter(inetUtils.findFirstNonLoopbackHostInfo());
    }

}
