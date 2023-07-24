package com.github.fanzezhen.common.gateway.core.tracing;

import com.github.fanzezhen.common.gateway.core.constant.CommonGatewayConstant;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @author
 */
public class OpenTracingFilter implements GlobalFilter, Ordered {

    private static final Log log = LogFactory.getLog(OpenTracingFilter.class);

    private Tracer tracer;

    public OpenTracingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Span http = tracer.buildSpan("serve").start();
        exchange.getAttributes().put(CommonGatewayConstant.OPEN_TRACING_SPAN, http);
        return chain.filter(exchange).doFinally((aVoid) -> finishSpan(exchange, http));
    }

    private void finishSpan(ServerWebExchange exchange, Span span) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            doFinishSpan(exchange, span);
        } else {
            response.beforeCommit(() -> {
                doFinishSpan(exchange, span);
                return Mono.empty();
            });
        }
    }

    private void doFinishSpan(ServerWebExchange exchange, Span span) {
        URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
        String path = requestUrl.getPath();
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        span.setTag("gateway.route", route.getId());
        span.setTag("gateway.url", path);
        HttpMethod httpMethod = exchange.getRequest().getMethod();
        span.log("htt method " + httpMethod.name());
        span.finish();
    }


}