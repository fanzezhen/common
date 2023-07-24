package com.github.fanzezhen.common.gateway.core.constant;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;

import java.util.Locale;

/**
 * 定义框架系统常量的类
 *
 * @author Thomason
 * @version 1.2
 * @since 2009-03-10
 */
public interface CommonGatewayConstant {
    Sign SIGN = SecureUtil.sign(SignAlgorithm.MD5withRSA);

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
