package com.github.fanzezhen.common.gateway.core.http.client.customizer.impl;

import com.github.fanzezhen.common.gateway.core.http.client.customizer.DoAfterRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientRequest;

import java.util.Map;

/**
 * @author zezhen.fan
 */
public class HeaderLogger implements DoAfterRequest {

    private static final Logger logger = LoggerFactory.getLogger(HeaderLogger.class);

    @Override
    public void accept(HttpClientRequest req, Connection connection) {
        if (logger.isDebugEnabled()) {
            logger.debug("request {}", req);
            HttpHeaders entries = req.requestHeaders();
            for (Map.Entry<String, String> headerLine : entries) {
                String key = headerLine.getKey();
                String value = headerLine.getValue();
                logger.debug("{}:{}", key, value);
            }
        }
    }
}
