package com.github.fanzezhen.common.gateway.sub.env;

import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;

import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.DispatcherHandler;

@Configuration
@ConditionalOnClass({ RibbonAutoConfiguration.class,
		DispatcherHandler.class })
@AutoConfigureAfter(RibbonAutoConfiguration.class)
@EnableConfigurationProperties(LoadBalancerProperties.class)
@PropertySource("classpath:subEnv.properties")
public class SubEnvAutoConfiguration {

	@Bean
	@ConditionalOnBean(RibbonLoadBalancerClient.class)
	public LoadBalancerClientFilter loadBalancerClientFilter(RibbonLoadBalancerClient client,
	                                                         LoadBalancerProperties properties) {
		return new SubEnvLoadBalancerClientFilter(client, properties);
	}

	@Bean
	public PropertiesFactory propertiesFactory() {
		return new SubEnvPropertiesFactory();
	}
}
