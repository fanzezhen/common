package com.github.fanzezhen.common.gateway.core.http.client.customizer.auto;

import com.github.fanzezhen.common.gateway.core.http.client.customizer.*;
import com.github.fanzezhen.common.gateway.core.http.client.customizer.DoOnContext;
import com.github.fanzezhen.common.gateway.core.http.client.customizer.impl.HeaderLogger;
import com.github.fanzezhen.common.gateway.core.http.client.customizer.TcpConfiguration;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;
import reactor.util.context.Context;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.cloud.gateway.config.HttpClientProperties.Pool.PoolType.DISABLED;
import static org.springframework.cloud.gateway.config.HttpClientProperties.Pool.PoolType.FIXED;

/**
 * @author zezhen.fan
 */
@Configuration
public class HttpClientCustomAutoConfiguration {
    @Resource
    private HttpClientProperties httpClientProperties;

    @Bean
    public TcpConfiguration connectionTimeout() {
        return new TcpConfiguration.ConfigConnectionTimeout(httpClientProperties);
    }

    @Bean
    public TcpConfiguration readTimeout() {
        return new TcpConfiguration.ReadTimeout(httpClientProperties);
    }

    @Bean
    public DoAfterRequest doAfterRequest() {
        return new HeaderLogger();
    }

    @SuppressWarnings("Duplicates")
    @Bean
    public HttpClient httpClient(List<TcpConfiguration> tcpConfigurations,
                                 List<DoOnContext> doOnContexts,
                                 List<DoOnRequest> doOnRequests,
                                 List<DoAfterRequest> doAfterRequests,
                                 List<DoOnError.Request> doOnRequestErrors,
                                 List<DoOnError.Response> doOnResponseErrors,
                                 List<DoOnResponse> doOnResponses) {


        // configure pool resources
        HttpClientProperties.Pool pool = httpClientProperties.getPool();
        ConnectionProvider connectionProvider;
        if (pool.getType() == DISABLED) {
            connectionProvider = ConnectionProvider.newConnection();
        } else if (pool.getType() == FIXED) {
            connectionProvider = ConnectionProvider.fixed(pool.getName(),
                    pool.getMaxConnections(), pool.getAcquireTimeout());
        } else {
            connectionProvider = ConnectionProvider.elastic(pool.getName());
        }

        return HttpClient.create(connectionProvider)
                .wiretap(true)
                .tcpConfiguration(tcpClient -> {
                    TcpClient tmpTcpClient = tcpClient;
                    for (TcpConfiguration tcpConfiguration : tcpConfigurations) {
                        tmpTcpClient = tcpConfiguration.apply(tmpTcpClient);
                    }
                    return tmpTcpClient;
                })
                .mapConnect((conn, boot) -> conn.subscriberContext(ctx -> {
                    Context context = ctx;
                    for (DoOnContext doOnContext : doOnContexts) {
                        context = doOnContext.apply(context);
                    }
                    return context;
                }))
                .doOnRequest((req, conn) -> {
                    for (DoOnRequest doOnRequest : doOnRequests) {
                        doOnRequest.accept(req, conn);
                    }
                })
                .doAfterRequest((req, conn) -> {
                    for (DoAfterRequest doAfterRequest : doAfterRequests) {
                        doAfterRequest.accept(req, conn);
                    }
                })
                .doOnError((req, err) -> {
                    for (DoOnError.Request onRequestError : doOnRequestErrors) {
                        onRequestError.accept(req, err);
                    }
                }, (res, err) -> {
                    for (DoOnError.Response onResponseError : doOnResponseErrors) {
                        onResponseError.accept(res, err);
                    }
                })
                .doOnResponse((res, conn) -> {
                    for (DoOnResponse doOnRespons : doOnResponses) {
                        doOnRespons.accept(res, conn);
                    }
                });
    }
}
