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

package com.github.fanzezhen.common.gateway.core.filter.retry;

import io.opentracing.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.event.EnableBodyCachingEvent;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.retry.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;

import static com.github.fanzezhen.common.gateway.core.constant.GatewayAttribute.OPEN_TRACING_SPAN;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CLIENT_RESPONSE_HEADER_NAMES;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ALREADY_ROUTED_ATTR;

/**
 * copy from {@link org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory}
 * add back off support
 * @author zezhen.fan
 */
public class FullRetryGatewayFilterFactory
        extends AbstractGatewayFilterFactory<FullRetryGatewayFilterFactory.RetryConfig> {

    /**
     * Retry iteration key.
     */
    public static final String RETRY_ITERATION_KEY = "retry_iteration";

    private static final Logger logger = LoggerFactory.getLogger(FullRetryGatewayFilterFactory.class);

    public FullRetryGatewayFilterFactory() {
        super(RetryConfig.class);
    }

    private static <T> List<T> toList(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    @Override
    public GatewayFilter apply(RetryConfig retryConfig) {
        retryConfig.validate();
        int backOffFactor = retryConfig.getBackOffFactor();
        long firstBackOffInMilli = retryConfig.getFirstBackOffInMilli();
        long maxBackOffInMilli = retryConfig.getMaxBackOffInMilli();
        if (backOffFactor < 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("backOffFactor is less than 0, set to 1");
            }
            backOffFactor = 1;
        }
        if (firstBackOffInMilli < 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("firstBackOffInMilli is less than 0, set to 200");
            }
            firstBackOffInMilli = 200;
        }
        if (maxBackOffInMilli < firstBackOffInMilli) {
            if (logger.isDebugEnabled()) {
                logger.debug("maxBackOffInMilli is less than firstBackOffInMilli, set to firstBackOffInMilli * 2");
            }
            maxBackOffInMilli = firstBackOffInMilli * 2;
        }
        Backoff exponentialBackoff = Backoff.exponential(Duration.ofMillis(firstBackOffInMilli),
                Duration.ofMillis(maxBackOffInMilli), backOffFactor, false);
        Repeat<ServerWebExchange> statusCodeRepeat = null;
        if (!retryConfig.getStatuses().isEmpty() || !retryConfig.getSeries().isEmpty()) {
            Predicate<RepeatContext<ServerWebExchange>> repeatPredicate = context -> {
                ServerWebExchange exchange = context.applicationContext();
                if (exceedsMaxIterations(exchange, retryConfig)) {
                    return false;
                }
                HttpStatus statusCode = exchange.getResponse().getStatusCode();
                boolean retryableStatusCode = retryConfig.getStatuses()
                        .contains(statusCode);
                // null status code
                if (!retryableStatusCode && statusCode != null) {
                    // network exception?
                    // try the series
                    retryableStatusCode = retryConfig.getSeries().stream()
                            .anyMatch(series -> statusCode.series().equals(series));
                }
                trace("retryableStatusCode: %b, statusCode %s, configured statuses %s, configured series %s",
                        retryableStatusCode, statusCode, retryConfig.getStatuses(),
                        retryConfig.getSeries());
                HttpMethod httpMethod = exchange.getRequest().getMethod();
                boolean retryableMethod = retryConfig.getMethods().contains(httpMethod);
                trace("retryableMethod: %b, httpMethod %s, configured methods %s",
                        retryableMethod, httpMethod, retryConfig.getMethods());
                return retryableMethod && retryableStatusCode;
            };
            statusCodeRepeat = Repeat.onlyIf(repeatPredicate)
                    .doOnRepeat(context -> reset(context.applicationContext()));
        }
        // TODO: support timeout, backoff, jitter, etc... in Builder
        Retry<ServerWebExchange> exceptionRetry = null;
        if (!retryConfig.getExceptions().isEmpty()) {
            Predicate<RetryContext<ServerWebExchange>> retryContextPredicate = context -> {
                if (exceedsMaxIterations(context.applicationContext(), retryConfig)) {
                    return false;
                }
                for (Class<? extends Throwable> clazz : retryConfig.getExceptions()) {
                    if (clazz.isInstance(context.exception())) {
                        trace("exception is retryable %s, configured exceptions",
                                context.exception().getClass().getName(), retryConfig.getExceptions());
                        return true;
                    }
                }
                trace("exception is not retryable %s, configured exceptions",
                        context.exception().getClass().getName(), retryConfig.getExceptions());
                return false;
            };
            exceptionRetry = Retry.onlyIf(retryContextPredicate)
                    .doOnRetry(context -> reset(context.applicationContext()))
                    .retryMax(retryConfig.getRetries())
                    .backoff(exponentialBackoff);
        }
        return apply(retryConfig.getRouteId(), statusCodeRepeat, exceptionRetry);
    }

    public boolean exceedsMaxIterations(ServerWebExchange exchange,
                                        RetryConfig retryConfig) {
        Integer iteration = exchange.getAttribute(RETRY_ITERATION_KEY);

        // TODO: deal with null iteration
        boolean exceeds = iteration != null && iteration >= retryConfig.getRetries();
        trace("exceedsMaxIterations %b, iteration %d, configured retries %d", exceeds,
                iteration, retryConfig.getRetries());
        return exceeds;
    }

    public void reset(ServerWebExchange exchange) {
        // TODO: what else to do to reset exchange?
        Set<String> addedHeaders = exchange.getAttributeOrDefault(
                CLIENT_RESPONSE_HEADER_NAMES, Collections.emptySet());
        addedHeaders
                .forEach(header -> exchange.getResponse().getHeaders().remove(header));
        exchange.getAttributes().remove(GATEWAY_ALREADY_ROUTED_ATTR);
    }

    @Deprecated
    public GatewayFilter apply(Repeat<ServerWebExchange> repeat,
                               Retry<ServerWebExchange> retry) {
        return apply(null, repeat, retry);
    }

    public GatewayFilter apply(String routeId, Repeat<ServerWebExchange> repeat,
                               Retry<ServerWebExchange> retry) {
        if (routeId != null && getPublisher() != null) {
            // send an event to enable caching
            getPublisher().publishEvent(new EnableBodyCachingEvent(this, routeId));
        }
        return (exchange, chain) -> {
            return chain.filter(exchange)
                    // .logger("retry-filter", Level.INFO)
                    .doOnSuccessOrError((aVoid, throwable) -> {
                        int iteration = exchange
                                .getAttributeOrDefault(RETRY_ITERATION_KEY, -1);
                        int newIteration = iteration + 1;
                        Span traceSpan = exchange.getAttribute(OPEN_TRACING_SPAN);
                        if (traceSpan != null) {
                            traceSpan.log(RETRY_ITERATION_KEY + ":" + newIteration + ";");
                        }
                        exchange.getAttributes().put(RETRY_ITERATION_KEY, newIteration);
                    }).retryWhen(retry.withApplicationContext(exchange));
        };
    }

    private void trace(String message, Object... args) {
        logger.info(String.format(message, args));
    }

    @SuppressWarnings("unchecked")
    public static class RetryConfig implements HasRouteId {

        private String routeId;

        private int retries = 3;

        private long firstBackOffInMilli = 200;
        private int backOffFactor = 1;
        private long maxBackOffInMilli = 1000 * 1000;

        private List<Series> series = toList(Series.SERVER_ERROR);

        private List<HttpStatus> statuses = new ArrayList<>();

        private List<HttpMethod> methods = toList(HttpMethod.GET);

        private List<Class<? extends Throwable>> exceptions = toList(IOException.class, NotFoundException.class);

        public RetryConfig allMethods() {
            return setMethods(HttpMethod.values());
        }

        public void validate() {
            Assert.isTrue(this.retries > 0, "retries must be greater than 0");
            Assert.isTrue(
                    !this.series.isEmpty() || !this.statuses.isEmpty()
                            || !this.exceptions.isEmpty(),
                    "series, status and exceptions may not all be empty");
            Assert.notEmpty(this.methods, "methods may not be empty");
        }

        @Override
        public String getRouteId() {
            return this.routeId;
        }

        @Override
        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        public int getRetries() {
            return retries;
        }

        public RetryConfig setRetries(int retries) {
            this.retries = retries;
            return this;
        }

        public List<Series> getSeries() {
            return series;
        }

        public RetryConfig setSeries(Series... series) {
            this.series = Arrays.asList(series);
            return this;
        }

        public List<HttpStatus> getStatuses() {
            return statuses;
        }

        public RetryConfig setStatuses(HttpStatus... statuses) {
            this.statuses = Arrays.asList(statuses);
            return this;
        }

        public List<HttpMethod> getMethods() {
            return methods;
        }

        public RetryConfig setMethods(HttpMethod... methods) {
            this.methods = Arrays.asList(methods);
            return this;
        }

        public List<Class<? extends Throwable>> getExceptions() {
            return exceptions;
        }

        public RetryConfig setExceptions(Class<? extends Throwable>... exceptions) {
            this.exceptions = Arrays.asList(exceptions);
            return this;
        }

        public long getFirstBackOffInMilli() {
            return firstBackOffInMilli;
        }

        public void setFirstBackOffInMilli(long firstBackOffInMilli) {
            this.firstBackOffInMilli = firstBackOffInMilli;
        }

        public int getBackOffFactor() {
            return backOffFactor;
        }

        public void setBackOffFactor(int backOffFactor) {
            this.backOffFactor = backOffFactor;
        }

        public long getMaxBackOffInMilli() {
            return maxBackOffInMilli;
        }

        public void setMaxBackOffInMilli(long maxBackOffInMilli) {
            this.maxBackOffInMilli = maxBackOffInMilli;
        }
    }

}
