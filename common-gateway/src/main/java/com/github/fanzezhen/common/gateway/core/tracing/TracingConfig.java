package com.github.fanzezhen.common.gateway.core.tracing;

import com.github.fanzezhen.common.gateway.core.http.client.customizer.*;
import com.github.fanzezhen.common.gateway.core.http.client.customizer.auto.HttpClientCustomAutoConfiguration;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerObjectFactory;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.metrics.Metrics;
import io.jaegertracing.internal.propagation.TextMapCodec;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.*;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.spi.Sampler;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

import static io.jaegertracing.internal.Constants.TRACER_IP_TAG_KEY;

/**
 * @author zezhen.fan
 */
@org.springframework.context.annotation.Configuration
@AutoConfigureAfter(HttpClientCustomAutoConfiguration.class)
@EnableConfigurationProperties({TracingProperties.class})
public class TracingConfig {

    @Resource
    private TracingProperties tracingProperties;

    @Value("${spring.application.name}")
    private String serviceName;

    @Resource
    InetUtils inetUtils;

    @Bean
    public Tracer tracer() {

        Reporter reporter = tracingProperties.getReporter().isUseConsole() ? new ConsoleReporter() : (new RemoteReporter.Builder()
                .withSender(new Configuration.SenderConfiguration().withEndpoint(tracingProperties.getSender().getEndpoint()).getSender())
                .withFlushInterval(tracingProperties.getReporter().getFlushIntervalMs())
                .withMaxQueueSize(tracingProperties.getReporter().getMaxQueueSize())
                .build());
        Configuration.SamplerConfiguration samplerConfiguration = new Configuration.SamplerConfiguration().withType(ConstSampler.TYPE)
                .withParam(tracingProperties.getSampler().getParam());

        JaegerTracer.Builder builder = new JaegerTracer.Builder(serviceName);

        JaegerObjectFactory jaegerObjectFactory = new JaegerObjectFactory();
        TextMapCodec textMapCodec =
                TextMapCodec.builder()
                        .withUrlEncoding(false)
                        .withSpanContextKey("common-opentracing-id")
                        .withObjectFactory(jaegerObjectFactory)
                        .build();
        builder.registerInjector(Format.Builtin.TEXT_MAP, textMapCodec);
        builder.registerExtractor(Format.Builtin.TEXT_MAP, textMapCodec);
        TextMapCodec httpCodec =
                TextMapCodec.builder()
                        .withUrlEncoding(true)
                        .withObjectFactory(jaegerObjectFactory)
                        .withSpanContextKey("common-opentracing-id")
                        .build();
        builder.registerInjector(Format.Builtin.HTTP_HEADERS, httpCodec);
        builder.registerExtractor(Format.Builtin.HTTP_HEADERS, httpCodec);

        JaegerTracer tracer = builder.withReporter(reporter)
                .withSampler(createSampler(samplerConfiguration, serviceName, null))
                .withTag(TRACER_IP_TAG_KEY, inetUtils.findFirstNonLoopbackHostInfo().getIpAddress())
                .build();
        return tracer;
    }

    @Bean
    public OpenTracingFilter openTracingFilter(Tracer tracer) {
        return new OpenTracingFilter(tracer);
    }

    @Bean
    public AddTraceHeaderGatewayFilterFactory addTraceHeaderGatewayFilterFactory() {
        return new AddTraceHeaderGatewayFilterFactory();
    }

    @Bean
    public DoOnContext onContext() {
        return new HttpClientTrace.OnContext();
    }

    @Bean
    public DoOnRequest onRequest() {
        return new HttpClientTrace.OnRequest();
    }

    @Bean
    public DoAfterRequest afterRequest() {
        return new HttpClientTrace.AfterRequest();
    }

    @Bean
    public DoOnError.Request onErrorRequest() {
        return new HttpClientTrace.OnRequestError();
    }

    @Bean
    public DoOnError.Response onErrorResponse() {
        return new HttpClientTrace.OnResponseError();
    }

    @Bean
    public DoOnResponse doOnResponse() {
        return new HttpClientTrace.OnResponse();
    }

    private Sampler createSampler(Configuration.SamplerConfiguration samplerConfiguration, String serviceName, Metrics metrics) {
        String samplerType = stringOrDefault(samplerConfiguration.getType(), RemoteControlledSampler.TYPE);
        Number samplerParam = numberOrDefault(samplerConfiguration.getParam(), ProbabilisticSampler.DEFAULT_SAMPLING_PROBABILITY);
        String hostPort = stringOrDefault(samplerConfiguration.getManagerHostPort(), HttpSamplingManager.DEFAULT_HOST_PORT);

        if (samplerType.equals(ConstSampler.TYPE)) {
            return new ConstSampler(samplerParam.intValue() != 0);
        }

        if (samplerType.equals(ProbabilisticSampler.TYPE)) {
            return new ProbabilisticSampler(samplerParam.doubleValue());
        }

        if (samplerType.equals(RateLimitingSampler.TYPE)) {
            return new RateLimitingSampler(samplerParam.intValue());
        }

        if (samplerType.equals(RemoteControlledSampler.TYPE)) {
            return new RemoteControlledSampler.Builder(serviceName)
                    .withSamplingManager(new HttpSamplingManager(hostPort))
                    .withInitialSampler(new ProbabilisticSampler(samplerParam.doubleValue()))
                    .withMetrics(metrics)
                    .build();
        }

        throw new IllegalStateException(String.format("Invalid sampling strategy %s", samplerType));
    }

    private static String stringOrDefault(String value, String defaultValue) {
        return value != null && value.length() > 0 ? value : defaultValue;
    }

    private static Number numberOrDefault(Number value, Number defaultValue) {
        return value != null ? value : defaultValue;
    }

    private static String getProperty(String name) {
        return System.getProperty(name, System.getenv(name));
    }
}
