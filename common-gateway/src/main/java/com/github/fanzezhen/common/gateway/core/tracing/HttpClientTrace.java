package com.github.fanzezhen.common.gateway.core.tracing;

import com.github.fanzezhen.common.gateway.core.http.client.customizer.*;
import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import reactor.util.context.Context;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public interface HttpClientTrace {
    String KEY_TRACE = "trace";

    class OnContext implements DoOnContext {

        @Autowired
        private Tracer tracer;

        @Override
        public Context apply(Context ctx) {
            return ctx.put(KEY_TRACE, new TraceCtx());
        }
    }

    class OnRequest implements DoOnRequest {

        @Autowired
        private Tracer tracer;

        @Override
        public void accept(HttpClientRequest req, Connection connection) {

        }
    }

    class AfterRequest implements DoAfterRequest {

        @Autowired
        private Tracer tracer;

        @Override
        public void accept(HttpClientRequest req, Connection connection) {
            Context ctx = req.currentContext();
            if (ctx.hasKey(KEY_TRACE)) {
                TraceCtx traceCtx = ctx.get(KEY_TRACE);
                HttpHeaders raw = req.requestHeaders();
                final HashMap<String, String> headers = new HashMap<>(8);
                for (Map.Entry<String, String> entry : raw) {
                    headers.put(entry.getKey(), entry.getValue());
                }

                SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(headers));

                if (parentSpan != null) {
                    Tracer.SpanBuilder spanBuilder = tracer.buildSpan("call").asChildOf(parentSpan);
                    tracer.buildSpan("call").asChildOf(parentSpan);
                    Span span = spanBuilder.start();
                    traceCtx.setSpan(span);
                }

                if (traceCtx.getSpan() != null) {
                    Span span = traceCtx.getSpan();
                    String serviceId = headers.get("X-Forwarded-Prefix");
                    HttpMethod method = req.method();
                    span.setTag("call.method", method.name());
                    if (StringUtil.isNotBlank(serviceId)) {
                        span.setTag("call.serviceId", serviceId);
                    }
                    String host = headers.get("host");
                    if (StringUtil.isNotBlank(host)) {
                        span.setTag("call.target", host);
                    }

                    SocketAddress socketAddress = connection.channel().remoteAddress();
                    if (socketAddress instanceof InetSocketAddress) {
                        String hostName = ((InetSocketAddress) socketAddress).getHostName();
                        span.setTag("call.host", hostName);
                    }
                }
            }
        }
    }

    class OnRequestError implements DoOnError.Request {

        @Override
        public void accept(HttpClientRequest req, Throwable throwable) {
            Context ctx = req.currentContext();
            if (ctx.hasKey(KEY_TRACE)) {
                TraceCtx traceCtx = ctx.get(KEY_TRACE);
                if (traceCtx.getSpan() != null) {
                    Span span = traceCtx.getSpan();
                    span.setTag("call.status", "error");
                    span.log(throwable.toString());
                    span.finish();
                }
            }
        }
    }

    class OnResponseError implements DoOnError.Response {

        @Override
        public void accept(HttpClientResponse res, Throwable throwable) {
            Context ctx = res.currentContext();
            if (ctx.hasKey(KEY_TRACE)) {
                TraceCtx traceCtx = ctx.get(KEY_TRACE);
                if (traceCtx.getSpan() != null) {
                    Span span = traceCtx.getSpan();
                    span.setTag("call.status", "error");
                    span.log(throwable.toString());
                    span.finish();
                }
            }
        }
    }

    class OnResponse implements DoOnResponse {

        @Override
        public void accept(HttpClientResponse res, Connection connection) {
            Context ctx = res.currentContext();
            if (ctx.hasKey(KEY_TRACE)) {
                TraceCtx traceCtx = ctx.get(KEY_TRACE);
                if (traceCtx.getSpan() != null) {
                    Span span = traceCtx.getSpan();
                    span.setTag("call.status", res.status().code() + "");
                    span.finish();
                }
            }
        }
    }

    class TraceCtx {


        private Span span;

        public void setSpan(Span span) {
            this.span = span;
        }

        public Span getSpan() {
            return span;
        }
    }
}
