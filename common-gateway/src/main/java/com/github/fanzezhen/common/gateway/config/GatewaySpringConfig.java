package com.github.fanzezhen.common.gateway.config;

import com.github.fanzezhen.common.gateway.core.discover.choose.Chooser;
import com.github.fanzezhen.common.gateway.core.discover.choose.HttpExposeChooser;
import com.github.fanzezhen.common.gateway.core.discover.eureka.DiscoverLocatorProperties;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.RouteConfigProvider;
import com.github.fanzezhen.common.gateway.core.discover.provider.route.StaticRouteConfigProvider;
import com.github.fanzezhen.common.gateway.core.filter.auth.AuthProperties;
import com.github.fanzezhen.common.gateway.core.filter.auth.factory.csp.CspTokenGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.core.filter.auth.factory.csp.FaqTokenGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.core.filter.ignore.IgnoreProperties;
import com.github.fanzezhen.common.gateway.core.filter.ignore.TokenIgnoreGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.core.metrics.DefaultExcludeMeterFilter;
import com.github.fanzezhen.common.gateway.core.support.error.GlobalErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.codec.ServerCodecConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.common.gateway.core")
@EnableConfigurationProperties({AuthProperties.class, IgnoreProperties.class})
public class GatewaySpringConfig {
    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ServerCodecConfigurer serverCodecConfigurer;

    @Bean
    public IgnoreProperties ignoreProperties() {
        return new IgnoreProperties();
    }

    @Bean
    public TokenIgnoreGatewayFilterFactory.DefinitionBuilder tokenDefinitionBuilder() {
        return new TokenIgnoreGatewayFilterFactory.DefinitionBuilder();
    }

    @Bean
    public TokenIgnoreGatewayFilterFactory tokenIgnoreGatewayFilterFactory() {
        return new TokenIgnoreGatewayFilterFactory();
    }

    @Bean
    public AuthProperties authProperties() {
        return new AuthProperties();
    }

    @Bean
    public CspTokenGatewayFilterFactory cspTokenGatewayFilterFactory(ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                                                                     AuthProperties authProperties) {
        return new CspTokenGatewayFilterFactory(reactiveStringRedisTemplate, authProperties);
    }

    @Bean
    public FaqTokenGatewayFilterFactory faqTokenGatewayFilterFactory(ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        return new FaqTokenGatewayFilterFactory(reactiveStringRedisTemplate);
    }

    //--------------global------------------//

    @Bean
    @Primary
    @DependsOn("reactiveRedisConnectionFactory")
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        RedisSerializer<String> serializer = new StringRedisSerializer();
        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
                .<String, String>newSerializationContext().key(serializer)
                .value(serializer).hashKey(serializer).hashValue(serializer).build();
        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory,
                serializationContext);
    }

    @Bean
    public RouteConfigProvider routeConfigProvider(DiscoverLocatorProperties discoverLocatorProperties,
                                                   List<GatewayFilterFactory> gatewayFilters) {
        return new StaticRouteConfigProvider(discoverLocatorProperties, gatewayFilters);
    }

    @Bean
    public DefaultExcludeMeterFilter defaultExcludeMeterFilter() {
        ArrayList<String> toExclude = new ArrayList<>();
        toExclude.add("logback");
        toExclude.add("jvm.buffer");
        return new DefaultExcludeMeterFilter(toExclude);
    }

    @Bean
    public DiscoverLocatorProperties discoverLocatorProperties() {
        DiscoverLocatorProperties properties = new DiscoverLocatorProperties();
        return properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public Chooser chooser() {
        return new HttpExposeChooser();
    }

}
