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

package com.github.fanzezhen.common.gateway.core.tracing;

import com.github.fanzezhen.common.gateway.core.constant.CommonGatewayConstant;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;


/**
 * @author Spencer Gibb
 */
public class AddTraceHeaderGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AddTraceHeaderGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AddTraceHeaderGatewayFilterFactory.class);

    @Autowired
    private Tracer tracer;

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
            SpanContext context = http.context();
            tracer.inject(context, Format.Builtin.HTTP_HEADERS, new HeaderCarrier(builder));
            String traceId = ((JaegerSpanContext)context).getTraceId();
            exchange.getResponse().getHeaders().add("opentracing-id", traceId);
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
