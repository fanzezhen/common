package com.github.fanzezhen.common.gateway.core.http.client.customizer;

import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientResponse;

import java.util.function.BiConsumer;

/**
 * @author zezhen.fan
 */
public interface DoOnResponse extends BiConsumer<HttpClientResponse, Connection> {
}
