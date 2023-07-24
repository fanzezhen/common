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

package com.github.fanzezhen.common.gateway.extranet;

import com.github.fanzezhen.common.gateway.core.constant.CommonGatewayConstant;
import com.github.fanzezhen.common.gateway.core.discover.DiscoverLocatorProperties;
import io.opentracing.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractChangeRequestUriGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author Spencer Gibb
 */
public class AddSignHeaderAndChangeRequestUriFactory
        extends AbstractChangeRequestUriGatewayFilterFactory<AddSignHeaderAndChangeRequestUriFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AddSignHeaderAndChangeRequestUriFactory.class);
    private final DiscoverLocatorProperties properties;

    public AddSignHeaderAndChangeRequestUriFactory(DiscoverLocatorProperties properties) {
        super(Config.class);
        this.properties = properties;
    }


    @Override
    protected Optional<URI> determineRequestUri(ServerWebExchange exchange, Config config) {
        return Optional.of(URI.create(properties.getIntranetHost()));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Span http = exchange.getAttribute(CommonGatewayConstant.OPEN_TRACING_SPAN);
            if (http == null) {
                logger.warn("no tracing span");
                return chain.filter(exchange);
            }
            ServerHttpRequest req = exchange.getRequest();
            ServerHttpRequest.Builder builder = req.mutate();
            Long nonce = System.currentTimeMillis();
            String data = String.valueOf(nonce) + req.getPath() + properties.getSecurity();
            builder.header("nonce", String.valueOf(nonce));
            builder.header("sign", new String(CommonGatewayConstant.SIGN.sign(data.getBytes(StandardCharsets.UTF_8))));
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    public static class Config {

    }

}
