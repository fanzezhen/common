package com.github.fanzezhen.common.gateway.core.constant;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;

/**
 * @author zezhen.fan
 */
public interface GatewayAttribute {

    String IS_URL_TOKEN_IGNORED = qualify("isURLTokenIgnored");

    String SERVICE_INSTANCE = qualify("serviceInstance");

    String OPEN_TRACING_SPAN = qualify("openTracingSpan");

    /**
     * qualify
     *
     * @param attr attr
     * @return String
     */
    private static String qualify(String attr) {
        return ServerWebExchangeUtils.class.getName() + "." + attr;
    }
}
