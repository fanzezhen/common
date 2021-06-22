package com.github.fanzezhen.common.gateway.sub.env;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * @author zezhen.fan
 */
@Configuration
@ConditionalOnClass({ RibbonAutoConfiguration.class,
		DispatcherHandler.class })
@AutoConfigureAfter(RibbonAutoConfiguration.class)
@EnableConfigurationProperties(LoadBalancerProperties.class)
@PropertySource("classpath:subEnv.properties")
public class SubEnvAutoConfiguration {

	@Bean
	public PropertiesFactory propertiesFactory() {
		return new SubEnvPropertiesFactory();
	}
}
