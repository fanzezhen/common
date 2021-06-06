package com.github.fanzezhen.common.gateway.core.http.client.customizer;

import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;

import java.util.function.BiConsumer;

/**
 * @author zezhen.fan
 */
public interface DoOnError {

	interface Request extends BiConsumer<HttpClientRequest, Throwable> {
	}

	interface Response extends BiConsumer<HttpClientResponse, Throwable> {
	}
}
