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

package com.github.fanzezhen.common.gateway.core.patch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.FilterArgsEvent;
import org.springframework.cloud.gateway.event.PredicateArgsEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.Validator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;

/**
 * {@link RouteLocator} that loads routes from a {@link RouteDefinitionLocator}.
 *
 * @author Spencer Gibb
 */
public class RouteDefinitionRouteLocatorWithErrorHanlde
        implements RouteLocator, BeanFactoryAware, ApplicationEventPublisherAware {

    /**
     * Default filters name.
     */
    public static final String DEFAULT_FILTERS = "defaultFilters";

    public static final Route ERROR_ROUTE;

    static {
        RouteDefinition def = new RouteDefinition();
        def.setId("err");
        def.setUri(URI.create("http://localhost/error"));
        def.setOrder(-100);
        def.setFilters(new ArrayList<>());
        Predicate<ServerWebExchange> p = t -> false;
        ERROR_ROUTE = Route.async(def).asyncPredicate(t -> Mono.just(p.test(t))).build();
    }

    protected final Log logger = LogFactory.getLog(getClass());
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final ConversionService conversionService;
    private final Map<String, RoutePredicateFactory> predicates = new LinkedHashMap<>();
    private final Map<String, GatewayFilterFactory> gatewayFilterFactories = new HashMap<>();
    private final GatewayProperties gatewayProperties;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private BeanFactory beanFactory;
    private ApplicationEventPublisher publisher;
    @Autowired
    private Validator validator;

    public RouteDefinitionRouteLocatorWithErrorHanlde(RouteDefinitionLocator routeDefinitionLocator,
                                                      List<RoutePredicateFactory> predicates,
                                                      List<GatewayFilterFactory> gatewayFilterFactories,
                                                      GatewayProperties gatewayProperties, ConversionService conversionService) {
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.conversionService = conversionService;
        initFactories(predicates);
        gatewayFilterFactories.forEach(
                factory -> this.gatewayFilterFactories.put(factory.name(), factory));
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    private void initFactories(List<RoutePredicateFactory> predicates) {
        predicates.forEach(factory -> {
            String key = factory.name();
            if (this.predicates.containsKey(key)) {
                this.logger.warn("A RoutePredicateFactory named " + key
                        + " already exists, class: " + this.predicates.get(key)
                        + ". It will be overwritten.");
            }
            this.predicates.put(key, factory);
            if (logger.isInfoEnabled()) {
                logger.info("Loaded RoutePredicateFactory [" + key + "]");
            }
        });
    }

    @Override
    public Flux<Route> getRoutes() {
        return this.routeDefinitionLocator.getRouteDefinitions()
                .map(def -> {
                    try {
                        return this.convertToRoute(def);
                    } catch (Throwable ex) {
                        logger.error("error convert a route", ex);
                        return ERROR_ROUTE;
                    }
                })
                .filter(r -> r != ERROR_ROUTE)
                .map(route -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("RouteDefinition matched: " + route.getId());
                    }
                    return route;
                });

        /*
         * TODO: trace logging if (logger.isTraceEnabled()) {
         * logger.trace("RouteDefinition did not match: " + routeDefinition.getId()); }
         */
    }

    private Route convertToRoute(RouteDefinition routeDefinition) {
        AsyncPredicate<ServerWebExchange> predicate = combinePredicates(routeDefinition);
        List<GatewayFilter> gatewayFilters = getFilters(routeDefinition);

        return Route.async(routeDefinition).asyncPredicate(predicate)
                .replaceFilters(gatewayFilters).build();
    }

    @SuppressWarnings("unchecked")
    List<GatewayFilter> loadGatewayFilters(String id,
                                           List<FilterDefinition> filterDefinitions) {
        ArrayList<GatewayFilter> ordered = new ArrayList<>(filterDefinitions.size());
        for (int i = 0; i < filterDefinitions.size(); i++) {
            FilterDefinition definition = filterDefinitions.get(i);
            GatewayFilterFactory factory = this.gatewayFilterFactories
                    .get(definition.getName());
            if (factory == null) {
                throw new IllegalArgumentException(
                        "Unable to find GatewayFilterFactory with name "
                                + definition.getName());
            }
            Map<String, String> args = definition.getArgs();
            if (logger.isDebugEnabled()) {
                logger.debug("RouteDefinition " + id + " applying filter " + args + " to "
                        + definition.getName());
            }

            Map<String, Object> properties = factory.shortcutType().normalize(args,
                    factory, this.parser, this.beanFactory);

            Object configuration = factory.newConfig();
            Object toBind = null;
            try {
                toBind = AopUtils.isAopProxy((configuration)) && (configuration) instanceof Advised ?
                        ((Advised) (configuration)).getTargetSource().getTarget() : (configuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (toBind == null) {
                continue;
            }
            Bindable<?> bindable = Bindable.ofInstance(toBind);
            BindHandler handler = new IgnoreTopLevelConverterNotFoundBindHandler();
            if (validator != null) {
                handler = new ValidationBindHandler(handler, validator);
            }
            List<ConfigurationPropertySource> propertySources = Collections.singletonList(new MapConfigurationPropertySource(properties));
            (new Binder(propertySources, null, conversionService)).bindOrCreate(factory.shortcutFieldPrefix(), bindable, handler);

            GatewayFilter gatewayFilter = factory.apply(configuration);
            if (this.publisher != null) {
                this.publisher.publishEvent(new FilterArgsEvent(this, id, properties));
            }
            if (gatewayFilter instanceof Ordered) {
                ordered.add(gatewayFilter);
            } else {
                ordered.add(new OrderedGatewayFilter(gatewayFilter, i + 1));
            }
        }

        return ordered;
    }

    private List<GatewayFilter> getFilters(RouteDefinition routeDefinition) {
        List<GatewayFilter> filters = new ArrayList<>();

        // TODO: support option to apply defaults after route specific filters?
        if (!this.gatewayProperties.getDefaultFilters().isEmpty()) {
            filters.addAll(loadGatewayFilters(DEFAULT_FILTERS,
                    this.gatewayProperties.getDefaultFilters()));
        }

        if (!routeDefinition.getFilters().isEmpty()) {
            filters.addAll(loadGatewayFilters(routeDefinition.getId(),
                    routeDefinition.getFilters()));
        }

        AnnotationAwareOrderComparator.sort(filters);
        return filters;
    }

    private AsyncPredicate<ServerWebExchange> combinePredicates(
            RouteDefinition routeDefinition) {
        List<PredicateDefinition> predicates = routeDefinition.getPredicates();
        AsyncPredicate<ServerWebExchange> predicate = lookup(routeDefinition,
                predicates.get(0));

        for (PredicateDefinition andPredicate : predicates.subList(1,
                predicates.size())) {
            AsyncPredicate<ServerWebExchange> found = lookup(routeDefinition,
                    andPredicate);
            predicate = predicate.and(found);
        }

        return predicate;
    }

    @SuppressWarnings("unchecked")
    private AsyncPredicate<ServerWebExchange> lookup(RouteDefinition route,
                                                     PredicateDefinition predicate) {
        RoutePredicateFactory<Object> factory = this.predicates.get(predicate.getName());
        if (factory == null) {
            throw new IllegalArgumentException("Unable to find RoutePredicateFactory with name " + predicate.getName());
        }
        Map<String, String> args = predicate.getArgs();
        if (logger.isDebugEnabled()) {
            logger.debug("RouteDefinition " + route.getId() + " applying " + args + " to " + predicate.getName());
        }

        Map<String, Object> properties = factory.shortcutType().normalize(args, factory, this.parser, this.beanFactory);
        Object configuration = factory.newConfig();
        Object toBind = null;
        try {
            toBind = AopUtils.isAopProxy((configuration)) && (configuration) instanceof Advised ?
                    ((Advised) (configuration)).getTargetSource().getTarget() : (configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (toBind == null) {
            if (this.publisher != null) {
                this.publisher.publishEvent(
                        new PredicateArgsEvent(this, route.getId(), properties));
            }
            return factory.applyAsync(configuration);
        }
        Bindable<?> bindable = Bindable.ofInstance(toBind);
        BindHandler handler = new IgnoreTopLevelConverterNotFoundBindHandler();
        if (validator != null) {
            handler = new ValidationBindHandler(handler, validator);
        }
        List<ConfigurationPropertySource> propertySources = Collections.singletonList(new MapConfigurationPropertySource(properties));
        (new Binder(propertySources, null, conversionService)).bindOrCreate(factory.shortcutFieldPrefix(), bindable, handler);
        if (this.publisher != null) {
            this.publisher.publishEvent(
                    new PredicateArgsEvent(this, route.getId(), properties));
        }
        return factory.applyAsync(configuration);
    }

}
