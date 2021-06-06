package com.github.fanzezhen.common.gateway.core.http.client.customizer.auto;

import com.github.fanzezhen.common.gateway.core.http.client.customizer.impl.Metrics;
import com.github.fanzezhen.common.gateway.core.http.client.customizer.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zezhen.fan
 */
@Configuration
@AutoConfigureAfter(HttpClientCustomAutoConfiguration.class)
public class HttpClientMetricsAutoConfiguration {

	@Resource
	MeterRegistry meterRegistry;

	@Bean
	public DoOnContext onContext() {
		return new Metrics.OnContext(meterRegistry);
	}

	@Bean
	public DoOnRequest onRequest() {
		return new Metrics.OnRequest();
	}

	@Bean
	public DoAfterRequest afterRequest(){
		return new Metrics.AfterRequest();
	}

	@Bean
	public DoOnError.Request onErrorRequest(){
		return new Metrics.OnRequestError();
	}

	@Bean
	public DoOnError.Response onErrorResponse(){
		return new Metrics.OnResponseError();
	}

	@Bean
	public DoOnResponse doOnResponse(){
		return new Metrics.OnResponse();
	}
}
