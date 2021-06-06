package com.github.fanzezhen.common.gateway.config;

import com.github.fanzezhen.common.gateway.core.discover.choose.Chooser;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import com.github.fanzezhen.common.gateway.intranet.CheckExtranetSignGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.intranet.DiscoverClientIntranetRouteDefinitionLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.common.gateway")
public class GatewayIntranetSpringConfig {

    @Bean
    public CheckExtranetSignGatewayFilterFactory checkExtranetSignGatewayFilterFactory(
            ReactiveStringRedisTemplate reactiveStringRedisTemplate, Environment environment) {
        return new CheckExtranetSignGatewayFilterFactory(reactiveStringRedisTemplate, environment);
    }

    @Bean
    @DependsOn({"discoveryClient", "routeConfigProvider"})
    @ConditionalOnBean({DiscoveryClient.class, RouteConfigProvider.class})
    public DiscoverClientIntranetRouteDefinitionLocator discoverClientIntranetRouteDefinitionLocator(
            DiscoveryClient discoveryClient, RouteConfigProvider routeConfigProvider, Chooser chooser) {
        return new DiscoverClientIntranetRouteDefinitionLocator(discoveryClient, routeConfigProvider, chooser);
    }

}
