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

package com.github.fanzezhen.common.gateway.core.discover.eureka;

import com.github.fanzezhen.common.gateway.core.filter.retry.FullRetryGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.core.patch.RouteDefinitionRouteLocatorWithErrorHanlde;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;
import org.springframework.cloud.gateway.config.*;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.cloud.gateway.filter.factory.*;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.headers.ForwardedHeadersFilter;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.cloud.gateway.filter.headers.RemoveHopByHopHeadersFilter;
import org.springframework.cloud.gateway.filter.headers.XForwardedHeadersFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.PrincipalNameKeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.handler.predicate.*;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.cloud.gateway.support.StringToZonedDateTimeConverter;
import org.springframework.cloud.loadbalancer.support.SimpleObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.ProxyProvider;

import javax.net.ssl.SSLException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import static org.springframework.cloud.gateway.config.HttpClientProperties.Pool.PoolType.DISABLED;
import static org.springframework.cloud.gateway.config.HttpClientProperties.Pool.PoolType.FIXED;

/**
 * @author Spencer Gibb
 */
@Configuration
@ConditionalOnProperty(name = "spring.cloud.gateway.enabled", matchIfMissing = true)
@EnableConfigurationProperties
@AutoConfigureBefore({HttpHandlerAutoConfiguration.class, WebFluxAutoConfiguration.class})
@AutoConfigureAfter({GatewayClassPathWarningAutoConfiguration.class})
@ConditionalOnClass(DispatcherHandler.class)
public class GatewayAutoConfiguration {

    @Bean
    public StringToZonedDateTimeConverter stringToZonedDateTimeConverter() {
        return new StringToZonedDateTimeConverter();
    }

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public RouteLocatorBuilder routeLocatorBuilder(
            ConfigurableApplicationContext context) {
        return new RouteLocatorBuilder(context);
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator(
            GatewayProperties properties) {
        return new PropertiesRouteDefinitionLocator(properties);
    }

    @Bean
    @ConditionalOnMissingBean(RouteDefinitionRepository.class)
    public InMemoryRouteDefinitionRepository inMemoryRouteDefinitionRepository() {
        return new InMemoryRouteDefinitionRepository();
    }

    @Bean
    @Primary
    public RouteDefinitionLocator routeDefinitionLocator(
            List<RouteDefinitionLocator> routeDefinitionLocators) {
        return new CompositeRouteDefinitionLocator(
                Flux.fromIterable(routeDefinitionLocators));
    }

    @Bean
    @DependsOn("webFluxConversionService")
    @ConditionalOnBean(FormattingConversionService.class)
    public RouteLocator routeDefinitionRouteLocator(GatewayProperties properties,
                                                    List<GatewayFilterFactory> gatewayFilters,
                                                    List<RoutePredicateFactory> predicates,
                                                    RouteDefinitionLocator routeDefinitionLocator,
                                                    @Qualifier("webFluxConversionService") ConversionService conversionService) {
        return new RouteDefinitionRouteLocatorWithErrorHanlde(routeDefinitionLocator, predicates,
                gatewayFilters, properties, conversionService);
    }

    @Bean
    @Primary
    public RouteLocator cachedCompositeRouteLocator(List<RouteLocator> routeLocators) {
        return new CachingRouteLocator(
                new CompositeRouteLocator(Flux.fromIterable(routeLocators)));
    }

    @Bean
    public RouteRefreshListener routeRefreshListener(
            ApplicationEventPublisher publisher) {
        return new RouteRefreshListener(publisher);
    }

    @Bean
    public FilteringWebHandler filteringWebHandler(List<GlobalFilter> globalFilters) {
        return new FilteringWebHandler(globalFilters);
    }

    @Bean
    public GlobalCorsProperties globalCorsProperties() {
        return new GlobalCorsProperties();
    }

    @Bean
    public RoutePredicateHandlerMapping routePredicateHandlerMapping(
            FilteringWebHandler webHandler, RouteLocator routeLocator,
            GlobalCorsProperties globalCorsProperties, Environment environment) {
        return new RoutePredicateHandlerMapping(webHandler, routeLocator,
                globalCorsProperties, environment);
    }

    @Bean
    public GatewayProperties gatewayProperties() {
        return new GatewayProperties();
    }

    // ConfigurationProperty beans

    @Bean
    public SecureHeadersProperties secureHeadersProperties() {
        return new SecureHeadersProperties();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cloud.gateway.forwarded.enabled", matchIfMissing = true)
    public ForwardedHeadersFilter forwardedHeadersFilter() {
        return new ForwardedHeadersFilter();
    }

    // HttpHeaderFilter beans

    @Bean
    public RemoveHopByHopHeadersFilter removeHopByHopHeadersFilter() {
        return new RemoveHopByHopHeadersFilter();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cloud.gateway.x-forwarded.enabled", matchIfMissing = true)
    public XForwardedHeadersFilter xForwardedHeadersFilter() {
        return new XForwardedHeadersFilter();
    }

    // GlobalFilter beans

    @Bean
    public AdaptCachedBodyGlobalFilter adaptCachedBodyGlobalFilter() {
        return new AdaptCachedBodyGlobalFilter();
    }

    @Bean
    public RemoveCachedBodyFilter removeCachedBodyFilter() {
        return new RemoveCachedBodyFilter();
    }

    @Bean
    public RouteToRequestUrlFilter routeToRequestUrlFilter() {
        return new RouteToRequestUrlFilter();
    }

    @Bean
    public ForwardRoutingFilter forwardRoutingFilter(
            ObjectProvider<DispatcherHandler> dispatcherHandler) {
        return new ForwardRoutingFilter(dispatcherHandler);
    }

    @Bean
    public ForwardPathFilter forwardPathFilter() {
        return new ForwardPathFilter();
    }

    @Bean
    public WebSocketService webSocketService() {
        return new HandshakeWebSocketService();
    }

    @Bean
    public WebsocketRoutingFilter websocketRoutingFilter(WebSocketClient webSocketClient,
                                                         WebSocketService webSocketService,
                                                         ObjectProvider<List<HttpHeadersFilter>> headersFilters) {
        return new WebsocketRoutingFilter(webSocketClient, webSocketService, headersFilters);
    }

    @Bean
    @DependsOn("validator")
    @ConditionalOnBean(Validator.class)
    public WeightCalculatorWebFilter weightCalculatorWebFilter(Validator validator,
                                                               ObjectProvider<RouteLocator> routeLocator) {
        return new WeightCalculatorWebFilter(routeLocator, new ConfigurationService(
                null, new SimpleObjectProvider<>(null), new SimpleObjectProvider<>(validator)));
    }

    @Bean
    public AfterRoutePredicateFactory afterRoutePredicateFactory() {
        return new AfterRoutePredicateFactory();
    }

    @Bean
    public BeforeRoutePredicateFactory beforeRoutePredicateFactory() {
        return new BeforeRoutePredicateFactory();
    }

    @Bean
    public BetweenRoutePredicateFactory betweenRoutePredicateFactory() {
        return new BetweenRoutePredicateFactory();
    }

    @Bean
    public CookieRoutePredicateFactory cookieRoutePredicateFactory() {
        return new CookieRoutePredicateFactory();
    }

    @Bean
    public HeaderRoutePredicateFactory headerRoutePredicateFactory() {
        return new HeaderRoutePredicateFactory();
    }

    @Bean
    public HostRoutePredicateFactory hostRoutePredicateFactory() {
        return new HostRoutePredicateFactory();
    }

    @Bean
    public MethodRoutePredicateFactory methodRoutePredicateFactory() {
        return new MethodRoutePredicateFactory();
    }

    @Bean
    public PathRoutePredicateFactory pathRoutePredicateFactory() {
        return new PathRoutePredicateFactory();
    }

    @Bean
    public QueryRoutePredicateFactory queryRoutePredicateFactory() {
        return new QueryRoutePredicateFactory();
    }

    @Bean
    public ReadBodyRoutePredicateFactory readBodyRoutePredicateFactory() {
        return new ReadBodyRoutePredicateFactory();
    }

    @Bean
    public RemoteAddrRoutePredicateFactory remoteAddrRoutePredicateFactory() {
        return new RemoteAddrRoutePredicateFactory();
    }

    @Bean
    @DependsOn("weightCalculatorWebFilter")
    public WeightRoutePredicateFactory weightRoutePredicateFactory() {
        return new WeightRoutePredicateFactory();
    }

    @Bean
    public CloudFoundryRouteServiceRoutePredicateFactory cloudFoundryRouteServiceRoutePredicateFactory() {
        return new CloudFoundryRouteServiceRoutePredicateFactory();
    }

    // GatewayFilter Factory beans

    @Bean
    public AddRequestHeaderGatewayFilterFactory addRequestHeaderGatewayFilterFactory() {
        return new AddRequestHeaderGatewayFilterFactory();
    }

    @Bean
    public AddRequestParameterGatewayFilterFactory addRequestParameterGatewayFilterFactory() {
        return new AddRequestParameterGatewayFilterFactory();
    }

    @Bean
    public AddResponseHeaderGatewayFilterFactory addResponseHeaderGatewayFilterFactory() {
        return new AddResponseHeaderGatewayFilterFactory();
    }

    @Bean
    public ModifyRequestBodyGatewayFilterFactory modifyRequestBodyGatewayFilterFactory() {
        return new ModifyRequestBodyGatewayFilterFactory();
    }

    @Bean
    public DedupeResponseHeaderGatewayFilterFactory dedupeResponseHeaderGatewayFilterFactory() {
        return new DedupeResponseHeaderGatewayFilterFactory();
    }

    @Bean
    public ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory() {
        return new ModifyResponseBodyGatewayFilterFactory(HandlerStrategies.withDefaults().messageReaders(), Collections.emptySet(), Collections.emptySet());
    }

    @Bean
    public PrefixPathGatewayFilterFactory prefixPathGatewayFilterFactory() {
        return new PrefixPathGatewayFilterFactory();
    }

    @Bean
    public PreserveHostHeaderGatewayFilterFactory preserveHostHeaderGatewayFilterFactory() {
        return new PreserveHostHeaderGatewayFilterFactory();
    }

    @Bean
    public RedirectToGatewayFilterFactory redirectToGatewayFilterFactory() {
        return new RedirectToGatewayFilterFactory();
    }

    @Bean
    public RemoveRequestHeaderGatewayFilterFactory removeRequestHeaderGatewayFilterFactory() {
        return new RemoveRequestHeaderGatewayFilterFactory();
    }

    @Bean
    public RemoveResponseHeaderGatewayFilterFactory removeResponseHeaderGatewayFilterFactory() {
        return new RemoveResponseHeaderGatewayFilterFactory();
    }

    @Bean(name = PrincipalNameKeyResolver.BEAN_NAME)
    @ConditionalOnBean(RateLimiter.class)
    @ConditionalOnMissingBean(KeyResolver.class)
    public PrincipalNameKeyResolver principalNameKeyResolver() {
        return new PrincipalNameKeyResolver();
    }

    @Bean
    @ConditionalOnBean({RateLimiter.class, KeyResolver.class})
    public RequestRateLimiterGatewayFilterFactory requestRateLimiterGatewayFilterFactory(
            RateLimiter rateLimiter, KeyResolver resolver) {
        return new RequestRateLimiterGatewayFilterFactory(rateLimiter, resolver);
    }

    @Bean
    public RewritePathGatewayFilterFactory rewritePathGatewayFilterFactory() {
        return new RewritePathGatewayFilterFactory();
    }

    @Bean
    public FullRetryGatewayFilterFactory fullRetryGatewayFilterFactory() {
        return new FullRetryGatewayFilterFactory();
    }

    @Bean
    public SetPathGatewayFilterFactory setPathGatewayFilterFactory() {
        return new SetPathGatewayFilterFactory();
    }

    @Bean
    public SecureHeadersGatewayFilterFactory secureHeadersGatewayFilterFactory(
            SecureHeadersProperties properties) {
        return new SecureHeadersGatewayFilterFactory(properties);
    }

    @Bean
    public SetRequestHeaderGatewayFilterFactory setRequestHeaderGatewayFilterFactory() {
        return new SetRequestHeaderGatewayFilterFactory();
    }

    @Bean
    public SetResponseHeaderGatewayFilterFactory setResponseHeaderGatewayFilterFactory() {
        return new SetResponseHeaderGatewayFilterFactory();
    }

    @Bean
    public RewriteResponseHeaderGatewayFilterFactory rewriteResponseHeaderGatewayFilterFactory() {
        return new RewriteResponseHeaderGatewayFilterFactory();
    }

    @Bean
    public SetStatusGatewayFilterFactory setStatusGatewayFilterFactory() {
        return new SetStatusGatewayFilterFactory();
    }

    @Bean
    public SaveSessionGatewayFilterFactory saveSessionGatewayFilterFactory() {
        return new SaveSessionGatewayFilterFactory();
    }

    @Bean
    public StripPrefixGatewayFilterFactory stripPrefixGatewayFilterFactory() {
        return new StripPrefixGatewayFilterFactory();
    }

    @Bean
    public RequestHeaderToRequestUriGatewayFilterFactory requestHeaderToRequestUriGatewayFilterFactory() {
        return new RequestHeaderToRequestUriGatewayFilterFactory();
    }

    @Bean
    public RequestSizeGatewayFilterFactory requestSizeGatewayFilterFactory() {
        return new RequestSizeGatewayFilterFactory();
    }


    @Configuration
    @ConditionalOnClass(HttpClient.class)
    protected static class NettyConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public HttpClient httpClient(HttpClientProperties properties) {

            // configure pool resources
            HttpClientProperties.Pool pool = properties.getPool();

            ConnectionProvider connectionProvider;
            if (pool.getType() == DISABLED) {
                connectionProvider = ConnectionProvider.newConnection();
            } else if (pool.getType() == FIXED) {
                connectionProvider = ConnectionProvider.create(pool.getName(), pool.getMaxConnections());
            } else {
                connectionProvider = ConnectionProvider.create(pool.getName());
            }

            HttpClient httpClient = HttpClient.create(connectionProvider)
                    .tcpConfiguration(tcpClient -> {

                        if (properties.getConnectTimeout() != null) {
                            tcpClient = tcpClient.option(
                                    ChannelOption.CONNECT_TIMEOUT_MILLIS,
                                    properties.getConnectTimeout());
                        }

                        // configure proxy if proxy host is set.
                        HttpClientProperties.Proxy proxy = properties.getProxy();

                        if (StringUtils.hasText(proxy.getHost())) {

                            tcpClient = tcpClient.proxy(proxySpec -> {
                                ProxyProvider.Builder builder = proxySpec
                                        .type(ProxyProvider.Proxy.HTTP)
                                        .host(proxy.getHost());

                                PropertyMapper map = PropertyMapper.get();

                                map.from(proxy::getPort).whenNonNull().to(builder::port);
                                map.from(proxy::getUsername).whenHasText()
                                        .to(builder::username);
                                map.from(proxy::getPassword).whenHasText()
                                        .to(password -> builder.password(s -> password));
                                map.from(proxy::getNonProxyHostsPattern).whenHasText()
                                        .to(builder::nonProxyHosts);
                            });
                        }
                        return tcpClient;
                    });

            HttpClientProperties.Ssl ssl = properties.getSsl();
            if (ssl.getTrustedX509CertificatesForTrustManager().length > 0
                    || ssl.isUseInsecureTrustManager()) {
                httpClient = httpClient.secure(sslContextSpec -> {
                    // configure ssl
                    SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

                    X509Certificate[] trustedX509Certificates = ssl
                            .getTrustedX509CertificatesForTrustManager();
                    if (trustedX509Certificates.length > 0) {
                        sslContextBuilder.trustManager(trustedX509Certificates);
                    } else if (ssl.isUseInsecureTrustManager()) {
                        sslContextBuilder
                                .trustManager(InsecureTrustManagerFactory.INSTANCE);
                    }

                    try {
                        sslContextSpec.sslContext(sslContextBuilder.build())
                                .handshakeTimeout(ssl.getHandshakeTimeout())
                                .closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout())
                                .closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout());
                    } catch (SSLException e) {
                        e.printStackTrace();
                    }
                });
            }

            // TODO: add configuration to turn on wiretap
            // httpClient = httpClient.wiretap(true);

            return httpClient;
        }

        @Bean
        public HttpClientProperties httpClientProperties() {
            return new HttpClientProperties();
        }

        @Bean
        public NettyRoutingFilter routingFilter(HttpClient httpClient,
                                                ObjectProvider<List<HttpHeadersFilter>> headersFilters,
                                                HttpClientProperties properties) {
            return new NettyRoutingFilter(httpClient, headersFilters, properties);
        }

        @Bean
        @DependsOn("gatewayProperties")
        @ConditionalOnBean(GatewayProperties.class)
        public NettyWriteResponseFilter nettyWriteResponseFilter(GatewayProperties properties) {
            return new NettyWriteResponseFilter(properties.getStreamingMediaTypes());
        }

        @Bean
        public ReactorNettyWebSocketClient reactorNettyWebSocketClient(
                HttpClient httpClient) {
            return new ReactorNettyWebSocketClient(httpClient);
        }

    }

//	@Configuration
//	@ConditionalOnClass({ HystrixObservableCommand.class, RxReactiveStreams.class })
//	protected static class HystrixConfiguration {
//
//		@Bean
//		public HystrixGatewayFilterFactory hystrixGatewayFilterFactory(
//				ObjectProvider<DispatcherHandler> dispatcherHandler) {
//			return new HystrixGatewayFilterFactory(dispatcherHandler);
//		}
//
//		@Bean
//		public FallbackHeadersGatewayFilterFactory fallbackHeadersGatewayFilterFactory() {
//			return new FallbackHeadersGatewayFilterFactory();
//		}
//
//	}

    @Configuration
    @ConditionalOnClass(Health.class)
    protected static class GatewayActuatorConfiguration {
        // fzz todo routePredicates
        @Bean
        @ConditionalOnAvailableEndpoint
        @DependsOn("routeDefinitionLocator")
        @ConditionalOnBean(RouteDefinitionLocator.class)
        public GatewayControllerEndpoint gatewayControllerEndpoint(RouteDefinitionLocator routeDefinitionLocator,
                                                                   List<GlobalFilter> globalFilters,
                                                                   List<GatewayFilterFactory> gatewayFilters,
                                                                   List<RoutePredicateFactory> routePredicates,
                                                                   RouteDefinitionWriter routeDefinitionWriter,
                                                                   RouteLocator routeLocator) {
            return new GatewayControllerEndpoint(globalFilters, gatewayFilters, routePredicates, routeDefinitionWriter,
                    routeLocator, routeDefinitionLocator);
        }
    }

}
