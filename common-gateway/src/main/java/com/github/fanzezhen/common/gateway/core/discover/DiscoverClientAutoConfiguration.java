package com.github.fanzezhen.common.gateway.core.discover;


import com.github.fanzezhen.common.gateway.core.discover.choose.Chooser;
import com.github.fanzezhen.common.gateway.core.discover.choose.HttpExposeChooser;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("WeakerAccess")
@Configuration
@AutoConfigureBefore(GatewayAutoConfiguration.class)
@AutoConfigureAfter(CompositeDiscoveryClientAutoConfiguration.class)
@EnableConfigurationProperties
public class DiscoverClientAutoConfiguration {


	@Bean
	@ConditionalOnBean(DiscoveryClient.class)
	public DiscoverClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator(
			DiscoveryClient discoveryClient, RouteConfigProvider routeConfigProvider, Chooser chooser) {
		return new DiscoverClientRouteDefinitionLocator(discoveryClient, routeConfigProvider, chooser);
	}

	@Bean
	public DiscoverLocatorProperties eurekaDiscoverLocatorProperties() {
		DiscoverLocatorProperties properties = new DiscoverLocatorProperties();
		return properties;
	}


	@Bean
	@ConditionalOnMissingBean
	public Chooser chooser(){
		return new HttpExposeChooser();
	}
}
