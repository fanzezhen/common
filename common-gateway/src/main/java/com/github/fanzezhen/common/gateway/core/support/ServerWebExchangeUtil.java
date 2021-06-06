package com.github.fanzezhen.common.gateway.core.support;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author zezhen.fan
 */
public class ServerWebExchangeUtil {

	public static Mono<Void> completeWithCode(ServerWebExchange exchange, HttpStatus httpStatus) {
		exchange.getResponse().setStatusCode(httpStatus);
		return  exchange.getResponse().setComplete();
	}
}
