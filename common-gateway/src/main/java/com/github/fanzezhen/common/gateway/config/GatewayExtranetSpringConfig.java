package com.github.fanzezhen.common.gateway.config;

import com.github.fanzezhen.common.gateway.core.discover.eureka.DiscoverLocatorProperties;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import com.github.fanzezhen.common.gateway.extranet.AddSignHeaderAndChangeRequestUriFactory;
import com.github.fanzezhen.common.gateway.extranet.DiscoverClientExtranetRouteDefinitionLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.common.gateway.extranet")
public class GatewayExtranetSpringConfig {

    @Resource
    private DiscoverLocatorProperties discoverLocatorProperties;

    @Bean
    public AddSignHeaderAndChangeRequestUriFactory addSignHeaderAndChangeRequestUriFactory() {
        return new AddSignHeaderAndChangeRequestUriFactory(discoverLocatorProperties);
    }

    @Bean
    @DependsOn("routeConfigProvider")
    @ConditionalOnBean(RouteConfigProvider.class)
    public DiscoverClientExtranetRouteDefinitionLocator discoveryClientRouteDefinitionLocator(
            RouteConfigProvider routeConfigProvider) {
        return new DiscoverClientExtranetRouteDefinitionLocator(routeConfigProvider);
    }

}
