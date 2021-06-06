package com.github.fanzezhen.common.gateway.core.discover.provider.route;

import com.github.fanzezhen.common.gateway.core.discover.eureka.DiscoverLocatorProperties;
import com.github.fanzezhen.common.gateway.core.filter.ignore.IgnoreProperties;
import com.github.fanzezhen.common.gateway.core.filter.ignore.TokenIgnoreGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.core.filter.retry.FullRetryGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.extranet.AddSignHeaderAndChangeRequestUriFactory;
import com.github.fanzezhen.common.gateway.core.tracing.AddTraceHeaderGatewayFilterFactory;
import com.github.fanzezhen.common.gateway.intranet.CheckExtranetSignGatewayFilterFactory;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.ConfigurationUtils;
import org.springframework.core.style.ToStringCreator;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory.REGEXP_KEY;
import static org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory.REPLACEMENT_KEY;
import static org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory.PATTERN_KEY;
import static org.springframework.cloud.gateway.support.NameUtils.normalizeFilterFactoryName;
import static org.springframework.cloud.gateway.support.NameUtils.normalizeRoutePredicateName;

/**
 * @author zezhen.fan
 */
public class StaticRouteConfigProvider implements RouteConfigProvider, BeanFactoryAware, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(StaticRouteConfigProvider.class);
    private final DiscoverLocatorProperties properties;
    private final String routeIdPrefix;
    private final SimpleEvaluationContext simpleEvaluationContext;
    private final Map<String, GatewayFilterFactory> gatewayFilterFactories = new HashMap<>();
    @Autowired
    private IgnoreProperties ignoreProperties;
    private BeanFactory beanFactory;
    private final static String ROUTE_ID_INTRANET_PREFIX = "intranet-";

    @Autowired
    private Validator validator;

    public StaticRouteConfigProvider(DiscoverLocatorProperties properties, List<GatewayFilterFactory> gatewayFilterFactories) {
        this.properties = properties;
        gatewayFilterFactories.forEach(
                factory -> this.gatewayFilterFactories.put(factory.name(), factory));
        if (StringUtils.hasText(properties.getRouteIdPrefix())) {
            this.routeIdPrefix = properties.getRouteIdPrefix();
        } else {
            this.routeIdPrefix = "api-";
        }
        simpleEvaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding().withInstanceMethods().build();
    }

    @Override
    public RouteDefinition loadRouteDefinition(ServiceInstance instance) {
        return loadRouteDefinition(instance, false);
    }

    @Override
    public RouteDefinition loadRouteDefinition(ServiceInstance instance, boolean checkExtranetSign) {
        String serviceId = instance.getServiceId();
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression urlExpr = parser.parseExpression(properties.getUrlExpression());
        String uri = urlExpr.getValue(simpleEvaluationContext, instance, String.class);
        String routeId = (checkExtranetSign ? ROUTE_ID_INTRANET_PREFIX : this.routeIdPrefix) + serviceId;
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(routeId.toLowerCase());
        routeDefinition.setUri(URI.create(uri));
        final ServiceInstance instanceForEval = new DelegatingServiceInstance(instance, properties);
        String urlPrefix = checkExtranetSign ? properties.getIntranetUrlPrefix() : properties.getContextPath();
        for (PredicateDefinition original : getPredicateChain(urlPrefix)) {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName(original.getName());
            for (Map.Entry<String, String> entry : original.getArgs().entrySet()) {
                String value = getValueFromExpr(simpleEvaluationContext, parser, instanceForEval, entry);
                predicate.addArg(entry.getKey(), value);
            }
            routeDefinition.getPredicates().add(predicate);
        }

        routeDefinition.setFilters(loadFilterChain(instanceForEval, routeId, parser, checkExtranetSign));

        return routeDefinition;
    }

    @Override
    public RouteDefinition loadRouteDefinition() {
        String routeId = "routeId-extranetToIntranet";
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(routeId.toLowerCase());
        routeDefinition.setUri(URI.create(properties.getIntranetHost()));
        ArrayList<PredicateDefinition> definitions = new ArrayList<>();
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName(normalizeRoutePredicateName(PathRoutePredicateFactory.class));
        predicate.addArg(PATTERN_KEY, properties.getContextPath() + "/api/**");
        definitions.add(predicate);
        routeDefinition.setPredicates(definitions);

        ArrayList<FilterDefinition> filterDefinitionList = new ArrayList<>();
        FilterDefinition rewritePathFilterDefinition = new FilterDefinition();
        rewritePathFilterDefinition.setName(normalizeFilterFactoryName(RewritePathGatewayFilterFactory.class));
        String regex = properties.getContextPath() + "/api/(?<remaining>.*)";
        String replacement = "/cloud/${remaining}";
        rewritePathFilterDefinition.addArg(REGEXP_KEY, regex);
        rewritePathFilterDefinition.addArg(REPLACEMENT_KEY, replacement);
        filterDefinitionList.add(rewritePathFilterDefinition);
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setName(normalizeFilterFactoryName(AddSignHeaderAndChangeRequestUriFactory.class));
        filterDefinitionList.add(filterDefinition);
        checkGatewayFilters(routeId, filterDefinitionList);
        routeDefinition.setFilters(filterDefinitionList);

        return routeDefinition;
    }

    /**
     * try catch block make sure even when we have a config error, the service will still be available
     *
     * @param instanceForEval
     * @param routeId
     * @param parser
     * @return
     */
    private List<FilterDefinition> loadFilterChain(ServiceInstance instanceForEval,
                                                   String routeId,
                                                   SpelExpressionParser parser,
                                                   boolean checkExtranetSign) {
        if (properties.isUseEurekaMeta()) {
            try {
                List<FilterDefinition> filterDefinitions = loadEurekaFilterChain(instanceForEval, routeId, parser, checkExtranetSign);
                checkGatewayFilters(routeId, filterDefinitions);
                return filterDefinitions;
            } catch (Throwable ex) {
                logger.error("error build route {} from meta", routeId, ex);
            }
        }
        return loadDefaultFilterChain(instanceForEval, routeId, parser, checkExtranetSign);
    }

    private List<FilterDefinition> loadEurekaFilterChain(ServiceInstance instance,
                                                         String routeId,
                                                         SpelExpressionParser parser,
                                                         boolean checkExtranetSign) {
        ArrayList<FilterDefinition> toAdd = new ArrayList<>();
        for (FilterDefinition original : doGetEurekaFilterChain(instance, routeId, checkExtranetSign)) {
            FilterDefinition filter = new FilterDefinition();
            filter.setName(original.getName());
            for (Map.Entry<String, String> entry : original.getArgs().entrySet()) {
                String value = getValueFromExpr(simpleEvaluationContext, parser, instance, entry);
                filter.addArg(entry.getKey(), value);
            }
            toAdd.add(filter);
        }
        return toAdd;
    }


    private List<FilterDefinition> loadDefaultFilterChain(ServiceInstance instance,
                                                          String routeId,
                                                          SpelExpressionParser parser,
                                                          boolean checkExtranetSign) {
        ArrayList<FilterDefinition> toAdd = new ArrayList<>();
        for (FilterDefinition original : doGetDefaultFilterChain(instance, routeId, checkExtranetSign)) {
            FilterDefinition filter = new FilterDefinition();
            filter.setName(original.getName());
            for (Map.Entry<String, String> entry : original.getArgs()
                    .entrySet()) {
                String value = getValueFromExpr(simpleEvaluationContext, parser,
                        instance, entry);
                filter.addArg(entry.getKey(), value);
            }
            toAdd.add(filter);
        }
        return toAdd;
    }

    @SuppressWarnings("Duplicates")
    private List<FilterDefinition> doGetEurekaFilterChain(ServiceInstance instance,
                                                          String routeId,
                                                          boolean checkExtranetSign) {
        ArrayList<FilterDefinition> definitions = new ArrayList<>();

        FilterDefinition rewrite = new FilterDefinition();
        rewrite.setName(normalizeFilterFactoryName(RewritePathGatewayFilterFactory.class));
        String regex = "'" + properties.getContextPath() + "/' + serviceId + '/(?<remaining>.*)'";
        String replacement = "'/${remaining}'";
        rewrite.addArg(REGEXP_KEY, regex);
        rewrite.addArg(REPLACEMENT_KEY, replacement);

        FilterDefinition trace = new FilterDefinition();
        trace.setName(normalizeFilterFactoryName(AddTraceHeaderGatewayFilterFactory.class));
        definitions.add(trace);

        FilterDefinition retry = new FilterDefinition();
        retry.setName(normalizeFilterFactoryName(FullRetryGatewayFilterFactory.class));
        retry.addArg("routeId", "'" + routeId + "'");
        //total three times
        retry.addArg("retries", "3");
        retry.addArg("methods", "'GET,POST,PUT'");
        retry.addArg("backOffFactor", "2");
        retry.addArg("firstBackOffInMilli", "2000");

        TokenIgnoreGatewayFilterFactory.DefinitionBuilder tokenBuilder =
                this.beanFactory.getBean(TokenIgnoreGatewayFilterFactory.DefinitionBuilder.class);
        FilterDefinition tokenIgnore = tokenBuilder.build(instance, routeId);
        FilterDefinition auth = new FilterDefinition();
        Map<String, String> metadata = instance.getMetadata();
        String filterName = "CspToken";
        String tokenFilterKey = "gateway.token.filter";
        if (metadata.containsKey(tokenFilterKey)) {
            filterName = metadata.get(tokenFilterKey) + "Token";
        }
        auth.setName(filterName);

        if (checkExtranetSign) {
            FilterDefinition extranetSign = new FilterDefinition();
            extranetSign.setName(normalizeFilterFactoryName(CheckExtranetSignGatewayFilterFactory.class));
            definitions.add(extranetSign);
        }
        definitions.add(rewrite);
        definitions.add(tokenIgnore);
        definitions.add(auth);
        definitions.add(retry);

        return definitions;
    }

    @SuppressWarnings("Duplicates")
    protected List<FilterDefinition> doGetDefaultFilterChain(ServiceInstance instance,
                                                             String routeId,
                                                             boolean checkExtranetSign) {
        ArrayList<FilterDefinition> definitions = new ArrayList<>();

        // add a rewrite that removes /serviceId by default
        FilterDefinition rewrite = new FilterDefinition();
        rewrite.setName(normalizeFilterFactoryName(RewritePathGatewayFilterFactory.class));
        String regex = "'" + properties.getContextPath() + "/' + serviceId + '/(?<remaining>.*)'";
        String replacement = "'/${remaining}'";
        rewrite.addArg(REGEXP_KEY, regex);
        rewrite.addArg(REPLACEMENT_KEY, replacement);
        definitions.add(rewrite);

        FilterDefinition trace = new FilterDefinition();
        trace.setName(normalizeFilterFactoryName(AddTraceHeaderGatewayFilterFactory.class));
        definitions.add(trace);

        FilterDefinition retry = new FilterDefinition();
        retry.setName(normalizeFilterFactoryName(FullRetryGatewayFilterFactory.class));
        retry.addArg("routeId", "'" + routeId + "'");
        //total three times
        retry.addArg("retries", "3");
        retry.addArg("methods", "'GET,POST,PUT'");
        retry.addArg("backOffFactor", "2");
        retry.addArg("firstBackOffInMilli", "2000");
        definitions.add(retry);

        FilterDefinition tokenIgnore = new FilterDefinition();
        tokenIgnore.setName(normalizeFilterFactoryName(TokenIgnoreGatewayFilterFactory.class));
        tokenIgnore.addArg(TokenIgnoreGatewayFilterFactory.IGNORE_URL_KEY, "'" + ignoreProperties.getUrlMap().get(instance.getServiceId()) + "'");
        definitions.add(tokenIgnore);
        if (checkExtranetSign) {
            FilterDefinition extranetSign = new FilterDefinition();
            extranetSign.setName(normalizeFilterFactoryName(CheckExtranetSignGatewayFilterFactory.class));
            definitions.add(extranetSign);
        }

        FilterDefinition auth = new FilterDefinition();
        auth.setName("CspToken");
        definitions.add(auth);
        return definitions;
    }

    private void checkGatewayFilters(String id, List<FilterDefinition> filterDefinitions) {

        SpelExpressionParser parser = new SpelExpressionParser();
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

            Map<String, Object> properties = factory.shortcutType().normalize(args,
                    factory, parser, this.beanFactory);

            Object configuration = factory.newConfig();

            ConfigurationUtils.bind(configuration, properties,
                    factory.shortcutFieldPrefix(), definition.getName(), validator);

            GatewayFilter gatewayFilter = factory.apply(configuration);
            if (logger.isDebugEnabled()) {
                logger.debug("gateway gatewayFilter {} ok", gatewayFilter.toString());
            }
        }
    }

    private List<PredicateDefinition> getPredicateChain(String urlPrefix) {
        ArrayList<PredicateDefinition> definitions = new ArrayList<>();
        // add a predicate that matches the url at /serviceId/**
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName(normalizeRoutePredicateName(PathRoutePredicateFactory.class));
        predicate.addArg(PATTERN_KEY, "'" + urlPrefix + "/'+serviceId+'/**'");
        definitions.add(predicate);
        return definitions;
    }

    private String getValueFromExpr(SimpleEvaluationContext evalCtxt, SpelExpressionParser parser,
                                    ServiceInstance instance, Map.Entry<String, String> entry) {
        try {
            Expression valueExpr = parser.parseExpression(entry.getValue());
            return valueExpr.getValue(evalCtxt, instance, String.class);
        } catch (ParseException | EvaluationException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to parse " + entry.getValue(), e);
            }
            return entry.getValue();
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * make sure default filter chain is valid
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    private static class TestServiceInstance implements ServiceInstance {

        @Override
        public String getServiceId() {
            return "test-service";
        }

        @Override
        public String getHost() {
            throw new NotImplementedException();
        }

        @Override
        public int getPort() {
            throw new NotImplementedException();
        }

        @Override
        public boolean isSecure() {
            throw new NotImplementedException();
        }

        @Override
        public URI getUri() {
            throw new NotImplementedException();
        }

        @Override
        public Map<String, String> getMetadata() {
            return new HashMap<>(0);
        }
    }

    private static class DelegatingServiceInstance implements ServiceInstance {

        final ServiceInstance delegate;

        private final DiscoverLocatorProperties properties;

        private DelegatingServiceInstance(ServiceInstance delegate,
                                          DiscoverLocatorProperties properties) {
            this.delegate = delegate;
            this.properties = properties;
        }

        @Override
        public String getServiceId() {
            if (properties.isLowerCaseServiceId()) {
                return delegate.getServiceId().toLowerCase();
            }
            return delegate.getServiceId();
        }

        @Override
        public String getHost() {
            return delegate.getHost();
        }

        @Override
        public int getPort() {
            return delegate.getPort();
        }

        @Override
        public boolean isSecure() {
            return delegate.isSecure();
        }

        @Override
        public URI getUri() {
            return delegate.getUri();
        }

        @Override
        public Map<String, String> getMetadata() {
            return delegate.getMetadata();
        }

        @Override
        public String getScheme() {
            return delegate.getScheme();
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("delegate", delegate)
                    .append("properties", properties).toString();
        }

    }
}
