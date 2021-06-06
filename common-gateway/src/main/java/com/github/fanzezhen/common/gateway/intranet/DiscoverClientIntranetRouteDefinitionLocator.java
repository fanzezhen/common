package com.github.fanzezhen.common.gateway.intranet;

import com.github.fanzezhen.common.gateway.core.discover.choose.Chooser;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import reactor.core.publisher.Flux;

/**
 * @author linjun.hu
 * @version 1.0
 * @date 2021/6/1 15:51
 */
public class DiscoverClientIntranetRouteDefinitionLocator implements RouteDefinitionLocator {
    private final DiscoveryClient discoveryClient;
    private RouteConfigProvider routeConfigProvider;
    private Chooser chooser;


    public DiscoverClientIntranetRouteDefinitionLocator(DiscoveryClient discoveryClient,
                                                        RouteConfigProvider routeConfigProvider,
                                                        Chooser chooser) {
        this.discoveryClient = discoveryClient;
        this.routeConfigProvider = routeConfigProvider;
        this.chooser = chooser;

    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(discoveryClient.getServices())
                .map(discoveryClient::getInstances)
                .filter(instances -> !instances.isEmpty())
                .filter(chooser)
                .map(instances -> instances.get(0))
                .map(instance -> routeConfigProvider.loadRouteDefinition(instance));
    }

}
