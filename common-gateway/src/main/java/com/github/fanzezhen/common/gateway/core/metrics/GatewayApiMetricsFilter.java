package com.github.fanzezhen.common.gateway.core.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @author Tony Clarke
 */
public class GatewayApiMetricsFilter implements GlobalFilter, Ordered {

	private static final Log log = LogFactory.getLog(org.springframework.cloud.gateway.filter.GatewayMetricsFilter.class);

	private MeterRegistry meterRegistry;


	public GatewayApiMetricsFilter(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Override
	public int getOrder() {
		// start the timer as soon as possible and report the metric event before we write
		// response to client
		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		Sample sample = Timer.start(meterRegistry);

		return chain.filter(exchange).doFinally((aVoid) -> endTimerRespectingCommit(exchange, sample));
	}

	private void endTimerRespectingCommit(ServerWebExchange exchange, Sample sample) {

		ServerHttpResponse response = exchange.getResponse();
		if (response.isCommitted()) {
			endTimerInner(exchange, sample);
		} else {
			response.beforeCommit(() -> {
				endTimerInner(exchange, sample);
				return Mono.empty();
			});
		}
	}

	private void endTimerInner(ServerWebExchange exchange, Sample sample) {

		String httpStatusCodeStr = "NA";

		String httpMethod = exchange.getRequest().getMethodValue();


		HttpStatus statusCode = exchange.getResponse().getStatusCode();
		if (statusCode != null) {
			httpStatusCodeStr = String.valueOf(statusCode.value());
		}


		// TODO refactor to allow Tags provider like in MetricsWebFilter
		Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);

		Tags tags = Tags.of("httpStatusCode", httpStatusCodeStr, "routeId", route.getId(), "httpMethod", httpMethod);

		if (log.isTraceEnabled()) {
			log.trace("gateway.requests tags: " + tags);
		}
		sample.stop(meterRegistry.timer("gateway.requests", tags));
	}


}