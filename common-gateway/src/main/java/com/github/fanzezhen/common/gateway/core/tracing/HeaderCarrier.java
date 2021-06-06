package com.github.fanzezhen.common.gateway.core.tracing;

import io.opentracing.propagation.TextMap;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Iterator;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public class HeaderCarrier implements TextMap {

    private ServerHttpRequest.Builder builder;

    public HeaderCarrier(ServerHttpRequest.Builder builder) {
        this.builder = builder;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }

    @Override
    public void put(String key, String value) {
            builder.header(key,value);
    }
}
