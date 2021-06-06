package com.github.fanzezhen.common.gateway.core.rate.limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author zezhen.fan
 */
public class RealIpResolver implements KeyResolver {

	private static Logger logger = LoggerFactory.getLogger(RealIpResolver.class);

	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {
		List<String> ipList = exchange.getRequest().getHeaders().get("X-Real-IP");
		if (logger.isDebugEnabled() && ipList != null) {
			logger.debug(String.join(",", ipList));
		}
		return ipList == null || ipList.size() == 0 ? Mono.empty() : Mono.just(ipList.get(0));
	}
}
