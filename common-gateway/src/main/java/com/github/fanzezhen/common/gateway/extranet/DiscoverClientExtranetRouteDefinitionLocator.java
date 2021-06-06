package com.github.fanzezhen.common.gateway.extranet;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.github.fanzezhen.common.gateway.core.discover.choose.Chooser;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import reactor.core.publisher.Flux;

/**
 * TODO: change to RouteLocator? use java dsl
 *
 * @author Spencer Gibb
 */
public class DiscoverClientExtranetRouteDefinitionLocator implements RouteDefinitionLocator {

	private static final Log log = LogFactory.get();

	private RouteConfigProvider routeConfigProvider;

	public DiscoverClientExtranetRouteDefinitionLocator(RouteConfigProvider routeConfigProvider) {
		this.routeConfigProvider = routeConfigProvider;

	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		RouteDefinition routeDefinition = routeConfigProvider.loadRouteDefinition();
		return Flux.just(routeDefinition);
	}





}

