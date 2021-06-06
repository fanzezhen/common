package com.github.fanzezhen.common.gateway.core.http.client.customizer;

import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientRequest;

import java.util.function.BiConsumer;

/**
 * @author zezhen.fan
 */
public interface DoOnRequest extends BiConsumer<HttpClientRequest, Connection> {
}
