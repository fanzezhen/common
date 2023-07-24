package com.github.fanzezhen.common.gateway.core.discover;

import com.github.fanzezhen.common.gateway.core.discover.choose.Chooser;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import reactor.core.publisher.Flux;

/**
 * TODO: change to RouteLocator? use java dsl
 *
 * @author Spencer Gibb
 */
public class DiscoverClientRouteDefinitionLocator implements RouteDefinitionLocator {

	private static final Log log = LogFactory
			.getLog(org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator.class);

	private final DiscoveryClient discoveryClient;
	private RouteConfigProvider routeConfigProvider;
	private Chooser chooser;

	public DiscoverClientRouteDefinitionLocator(DiscoveryClient discoveryClient,
                                                RouteConfigProvider routeConfigProvider, Chooser chooser) {
		this.discoveryClient = discoveryClient;
		this.routeConfigProvider = routeConfigProvider;
		this.chooser = chooser;

	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		RouteDefinition routeDefinition = routeConfigProvider.loadRouteDefinition();
		return Flux.just(routeDefinition);
	}





}

