/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fanzezhen.common.gateway.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.event.EnableBodyCachingEvent;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @author zezhen.fan
 */
public class AdaptCachedBodyGlobalFilter
		implements GlobalFilter, Ordered, ApplicationListener<EnableBodyCachingEvent> {

	private ConcurrentMap<String, Boolean> routesToCache = new ConcurrentHashMap<>();

	private static Logger logger = LoggerFactory.getLogger(AdaptCachedBodyGlobalFilter.class);

	/**
	 * Cached request body key.
	 */
	@Deprecated
	public static final String CACHED_REQUEST_BODY_KEY = CACHED_REQUEST_BODY_ATTR;

	@Override
	public void onApplicationEvent(EnableBodyCachingEvent event) {
		this.routesToCache.putIfAbsent(event.getRouteId(), true);
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// the cached ServerHttpRequest is used when the ServerWebExchange can not be
		// mutated, for example, during a predicate where the body is read, but still
		// needs to be cached.
		ServerHttpRequest cachedRequest = exchange
				.getAttributeOrDefault(CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR, null);
		if (cachedRequest != null) {
			exchange.getAttributes().remove(CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR);
			return chain.filter(exchange.mutate().request(cachedRequest).build());
		}

		//
		DataBuffer body = exchange.getAttributeOrDefault(CACHED_REQUEST_BODY_ATTR, null);
		Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);

		if (body != null || !this.routesToCache.containsKey(route.getId())) {
			return chain.filter(exchange);
		}

		return cacheRequestBody(exchange,false,
						(serverHttpRequest) -> chain.filter(
								exchange.mutate().request(serverHttpRequest).build()));
	}

	@SuppressWarnings("SameParameterValue")
	private static <T> Mono<T> cacheRequestBody(ServerWebExchange exchange,
	                                            boolean cacheDecoratedRequest,
	                                            Function<ServerHttpRequest, Mono<T>> function) {
		// Join all the DataBuffers so we have a single DataBuffer for the body
		return DataBufferUtils.join(exchange.getRequest().getBody())
				.defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[]{}))
				.flatMap(dataBuffer -> {
					if (dataBuffer.readableByteCount() > 0) {
						if (logger.isTraceEnabled()) {
							logger.trace("retaining body in exchange attribute");
						}
						exchange.getAttributes().put(CACHED_REQUEST_BODY_ATTR,
								dataBuffer);
					}

					ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
							exchange.getRequest()) {
						@Override
						public Flux<DataBuffer> getBody() {
							return Mono.<DataBuffer>fromSupplier(() -> {
								if (exchange.getAttributeOrDefault(
										CACHED_REQUEST_BODY_ATTR, null) == null) {
									// probably == downstream closed
									return null;
								}
								// TODO: deal with Netty
								NettyDataBuffer pdb = (NettyDataBuffer) dataBuffer;
								return pdb.factory()
										.wrap(pdb.getNativeBuffer().retainedSlice());
							}).flux();
						}
					};
					if (cacheDecoratedRequest) {
						exchange.getAttributes().put(
								CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR, decorator);
					}
					return function.apply(decorator);
				});
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1000;
	}

}
