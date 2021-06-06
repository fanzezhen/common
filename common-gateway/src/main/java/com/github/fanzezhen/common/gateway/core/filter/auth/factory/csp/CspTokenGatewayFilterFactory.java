package com.github.fanzezhen.common.gateway.core.filter.auth.factory.csp;


import com.fasterxml.jackson.core.type.TypeReference;
import com.github.fanzezhen.common.gateway.core.constant.GatewayAttribute;
import com.github.fanzezhen.common.gateway.core.constant.SystemConstant;
import com.github.fanzezhen.common.gateway.core.filter.auth.AuthProperties;
import com.github.fanzezhen.common.gateway.core.support.I18nUtil;
import com.github.fanzezhen.common.gateway.core.support.SerializeUtils;
import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import com.github.fanzezhen.common.gateway.core.support.response.ActionResult;
import com.github.fanzezhen.common.gateway.core.support.response.ErrorInfo;
import io.opentracing.Span;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.*;

import static com.github.fanzezhen.common.gateway.core.constant.GatewayAttribute.OPEN_TRACING_SPAN;


/**
 * @author zezhen.fan
 */
@SuppressWarnings("Duplicates")
public class CspTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<CspTokenGatewayFilterFactory.Config> {

    private static Logger logger = LoggerFactory.getLogger(CspTokenGatewayFilterFactory.class);

    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    private AuthProperties authProperties;

    public CspTokenGatewayFilterFactory(ReactiveStringRedisTemplate reactiveStringRedisTemplate, AuthProperties authProperties) {
        super(Config.class);
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.authProperties = authProperties;
    }

    private final static String USER_DATA_KEY_KICK = "kick";

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Boolean ignored = exchange.getAttribute(GatewayAttribute.IS_URL_TOKEN_IGNORED);
            if (ignored != null) {
                if (ignored) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ignored");
                    }
                    return chain.filter(exchange);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("GatewayAttribute.IS_URL_TOKEN_IGNORED is not set");
                }
            }
            ServerHttpRequest request = exchange.getRequest();
            Optional<String> tokenP = authProperties.getCspTokenTransferMode().getToken(request);
            if (tokenP.isEmpty()) {
                logger.info("empty token");
                return write405(exchange);
            }
            String token = tokenP.get();
            String redisTokenKey = "sso:token:" + token;
            try {
                Mono<String> userInfoMono = reactiveStringRedisTemplate.opsForValue().get(redisTokenKey);
                return userInfoMono.defaultIfEmpty("")
                        .onErrorResume(throwable -> {
                            logger.error("error request sso session", throwable);
                            return Mono.just("");
                        })
                        .flatMap(tokenJson -> {
                            //no json, 405
                            if (StringUtils.isBlank(tokenJson)) {
                                logger.info("unable to retrieve session info");
                                return write405(exchange);
                            }
                            //no data, 405
                            Map<String, Object> userData = SerializeUtils.fromJson(tokenJson, new TypeReference<Map<String, Object>>() {
                            });
                            if (userData == null) {
                                logger.info("unable to deserialize session info");
                                return write405(exchange);
                            }
                            String tenantId = (String) userData.get("tenantId");
                            //kick
                            String tokenStatus = (String) userData.get("tokenStatus");
                            if (StringUtil.equalsIgnoreCase(tokenStatus, USER_DATA_KEY_KICK)) {
                                logger.info("kick {}", token);
                                return write406(tenantId, exchange);
                            }
                            String envToken = getHeader(request, SystemConstant.ENVIRONMENT_TOKEN);
                            boolean hasEnvToken = StringUtil.isNotBlank(envToken)
                                    && (!"null".equalsIgnoreCase(envToken))
                                    && (!"undefined".equalsIgnoreCase(envToken));
                            if (logger.isDebugEnabled()) {
                                logger.debug("has env token {}, val {}", hasEnvToken, hasEnvToken ? envToken : "");
                            }
                            ServerHttpRequest.Builder mutate = request.mutate();
                            mutate.header(SystemConstant.HEADER_TOKEN, token);
                            extractForwardedValues(request, mutate);
                            mutateCommonFromCookie(mutate, request, userData);
                            extendCookie(redisTokenKey, (String) userData.get("platform"));
                            return hasEnvToken ? dealHasEnvToken(exchange, chain, token, redisTokenKey, envToken, mutate) :
                                    dealNoEnvToken(exchange, chain, userData, mutate);
                        });
            } catch (Exception ex) {
                logger.error("wtf ", ex);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }

    @NotNull
    private Mono<Void> dealHasEnvToken(ServerWebExchange exchange,
                                       org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
                                       String token,
                                       String redisTokenKey,
                                       String envToken,
                                       ServerHttpRequest.Builder mutate) {
        String envTokenListKey = redisTokenKey + ":envList";
        String environmentRedisKey = "sso:environment_token:" + envToken;
        Flux<String> range = reactiveStringRedisTemplate.opsForList().range(envTokenListKey, 0, -1);
        Mono<String> checkEnvMono = range
                .onErrorResume(ex -> {
                    logger.error("error getting env list", ex);
                    return Mono.empty();
                })
                .filter(s -> s.equals(envToken))
                .single("");

        Collection<String> ids = new ArrayList<>();
        ids.add("tenantId");
        ids.add("projectId");
        ids.add("appId");
        ids.add("userId");
        Mono<List<String>> tokenMapMono = reactiveStringRedisTemplate.<String, String>opsForHash().multiGet(environmentRedisKey, ids)
                .onErrorResume(throwable -> {
                    logger.error("error getting env info", throwable);
                    List<String> defaultNilList = new ArrayList<>();
                    defaultNilList.add(null);
                    defaultNilList.add(null);
                    defaultNilList.add(null);
                    defaultNilList.add(null);
                    return Mono.just(defaultNilList);
                });
        return tokenMapMono.zipWith(checkEnvMono, (tokenInfo, checked) -> {
            if (StringUtil.isBlank(checked)) {
                logger.info("unable to find the env token {} in current cookie {}", envToken, token);
                return -1;
            }
            String envTenantId = panicGet(tokenInfo, 0);
            String projectId = panicGet(tokenInfo, 1);
            String appId = panicGet(tokenInfo, 2);
            String userId = panicGet(tokenInfo, 3);

            if (StringUtil.isBlank(envTenantId)) {
                logger.info("cannot find env tenant id {}", envToken);
                return -1;
            }
            if (StringUtil.isBlank(appId)) {
                logger.info("cannot find env app id {}", envToken);
                return -1;
            }

            mutate.header(SystemConstant.HEADER_TENANT_ID, envTenantId);
            mutate.header(SystemConstant.HEADER_APP_ID, appId);

            if (StringUtil.isNotBlank(userId)) {
                mutate.header(SystemConstant.HEADER_USER_ID, userId);
            }
            if (StringUtil.isNotBlank(projectId)) {
                mutate.header(SystemConstant.HEADER_PROJECT_ID, projectId);
            }
            return 1;
        }).flatMap(ret -> {
            if (ret < 0) {
                return write407(exchange);
            } else {
                ServerWebExchange newExchange = exchange.mutate().request(mutate.build()).build();
                return chain.filter(newExchange);
            }
        });
    }

    private Mono<Void> dealNoEnvToken(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain, Map<String, Object> userData, ServerHttpRequest.Builder mutate) {
        if (logger.isDebugEnabled()) {
            logger.debug("no env");
        }
        mutateCoreFromCookie(mutate, userData);
        ServerWebExchange newExchange = exchange.mutate().request(mutate.build()).build();
        return chain.filter(newExchange);
    }

    private void extendCookie(String tokenKey, String platform) {
        platform = (platform == null) ? "" : platform;
        if (authProperties.getExpireTimeInSeconds() > 0) {
            //todo 判断URL是否是用户操作产生的URL，如果不是用户操作产生的URL，则不需要延期
            Integer expireTime = getExpireSecondsByPlatform(platform);
            reactiveStringRedisTemplate.expire(tokenKey, Duration.ofSeconds(expireTime))
                    .onErrorResume(throwable -> {
                        logger.error("refresh expire cookie", throwable);
                        return Mono.empty();
                    }).subscribe();
            reactiveStringRedisTemplate.expire(tokenKey + ":envList", Duration.ofSeconds(expireTime))
                    .onErrorResume(throwable -> {
                        logger.error("refresh expire env list", throwable);
                        return Mono.empty();
                    }).subscribe();
            reactiveStringRedisTemplate.opsForList().range(tokenKey + ":envList", 0, -1)
                    .onErrorResume(throwable -> {
                        logger.error("refresh expire env list", throwable);
                        return Mono.empty();
                    })
                    .flatMap(env -> reactiveStringRedisTemplate.expire("sso:environment_token:" + env, Duration.ofSeconds(expireTime))
                            .onErrorResume(throwable -> {
                                logger.error("refresh expire env token {}", env, throwable);
                                return Mono.empty();
                            })
                    ).subscribe();
        }
    }

    private String panicGet(List<String> list, int index) {
        if (list != null && list.size() >= index + 1) {
            return list.get(index);
        }
        return null;
    }

    public Mono<Void> write405(ServerWebExchange exchange) {
        Span span = exchange.getAttribute(OPEN_TRACING_SPAN);
        if (span != null) {
            logger.warn("no tracing span");
            span.log("write405;");
        }
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return exchange.getResponse().writeWith(authFail("", "405", "token is null or not exists", "")
                .map(action -> exchange.getResponse().bufferFactory().wrap(SerializeUtils.toJsonBytes(action)))
        );
    }

    public Mono<Void> write406(String tenantId, ServerWebExchange exchange) {
        Span span = exchange.getAttribute(OPEN_TRACING_SPAN);
        if (span != null) {
            logger.warn("no tracing span");
            span.log("write406;");
        }
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return exchange.getResponse().writeWith(authFail(tenantId, "406", "current account has been logged in elsewhere", "")
                .map(action -> exchange.getResponse().bufferFactory().wrap(SerializeUtils.toJsonBytes(action)))
        );
    }

    public Mono<Void> write407(ServerWebExchange exchange) {
        Span span = exchange.getAttribute(OPEN_TRACING_SPAN);
        if (span != null) {
            logger.warn("no tracing span");
            span.log("write407;");
        }
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return exchange.getResponse().writeWith(authFail("", "407", "current env token does not match current cookie", "")
                .map(action -> exchange.getResponse().bufferFactory().wrap(SerializeUtils.toJsonBytes(action)))
        );
    }

    private ServerHttpRequest.Builder mutateCoreFromCookie(ServerHttpRequest.Builder builder, Map<String, Object> userData) {
        String userId = (String) userData.get("userId");
        if (userId != null) {
            builder = builder.header(SystemConstant.HEADER_USER_ID, userId);
        }

        String tenantId = (String) userData.get("tenantId");
        if (tenantId != null) {
            builder = builder.header(SystemConstant.HEADER_TENANT_ID, tenantId);
        }
        return builder;
    }

    private ServerHttpRequest.Builder mutateCommonFromCookie(ServerHttpRequest.Builder mutate, ServerHttpRequest request, Map<String, Object> userData) {
        String accountId = (String) userData.get("accountId");
        if (accountId != null) {
            mutate = mutate.header(SystemConstant.HEADER_ACCOUNT_ID, accountId);
        }
        try {
            String accountName = (String) userData.get("accountName");
            if (accountName != null) {
                mutate = mutate.header(SystemConstant.HEADER_ACCOUNT_NAME, URLEncoder.encode(accountName, "utf-8"));
            }
            String userName = (String) userData.get("userName");
            if (userName != null) {
                mutate = mutate.header(SystemConstant.HEADER_USER_NAME, URLEncoder.encode(userName, "utf-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("encoding failed");
        }
        String platform = (String) userData.get("platform");
        if (platform != null) {
            mutate = mutate.header(SystemConstant.HEADER_PLATFORM, platform);
        }
        String device = (String) userData.get("device");
        if (device != null) {
            mutate = mutate.header(SystemConstant.HEADER_DEVICE, device);
        }
        //语言特殊处理一下，如果用户信息中设置了语言，就用用户信息中设置的语言，否则用cookie中的locale值
        String locale = (String) userData.get("languageType");
        String localHeader = SystemConstant.CONTEXT_HEADER_PREFIX + "Locale";
        String local = "locale";
        if (StringUtils.isNotBlank(locale)) {
            mutate = mutate.header(localHeader, locale);
        } else if (StringUtils.isNotBlank(getCookieValue(request, local))) {
            mutate = mutate.header(localHeader, getCookieValue(request, "locale"));
        }
        //用户时区
        String timeZone = (String) userData.get("timeZone");
        if (StringUtils.isNotBlank(timeZone)) {
            mutate = mutate.header(SystemConstant.CONTEXT_HEADER_PREFIX + "TimeZone", timeZone);
        }
        return mutate;
    }

    private ServerHttpRequest.Builder extractForwardedValues(ServerHttpRequest request, ServerHttpRequest.Builder mutate) {
        List<String> xForwardedValues = request.getHeaders()
                .get("X-Forwarded-For");
        if (xForwardedValues == null || xForwardedValues.isEmpty()) {
            logger.warn("X-Forwarded-For header is empty");
            return mutate;
        }
        if (xForwardedValues.size() > 1) {
            logger.warn("Multiple X-Forwarded-For headers found, discarding all");
            return mutate;
        }
        List<String> values = Arrays.asList(xForwardedValues.get(0).split(","));
        if (values.size() == 1 && Strings.isBlank(values.get(0))) {
            return mutate;
        }
        String ip = values.get(0);
        return mutate.header(SystemConstant.HEADER_USER_IP, StringUtils.trim(ip));
    }

    private String getCookieValue(ServerHttpRequest request, String locale) {
        List<HttpCookie> tokens = request.getCookies().get("tokens");
        return CollectionUtils.isEmpty(tokens) ? null : tokens.get(0).getValue();
    }

    private String getHeader(ServerHttpRequest request, String environmentToken) {
        List<String> tokens = request.getHeaders().get(environmentToken);
        if (tokens != null && tokens.size() > 1) {
            logger.warn("found more than one tokens: {}", tokens.size());
        }
        return CollectionUtils.isEmpty(tokens) ? null : tokens.get(0);
    }

    private Integer getExpireSecondsByPlatform(String platform) {
        int expireSeconds = authProperties.getExpireTimeInSeconds();
        String platformPc = "pcapp";
        String platformMobile = "mobileapp";
        String platformWx = "wx";
        if (platformPc.equals(platform) || platformMobile.equals(platform) || platformWx.equals(platform)) {
            expireSeconds = authProperties.getExpireTimeAppInSeconds();
        }
        return expireSeconds;

    }

    public Mono<ActionResult> authFail(String tenantId, String errorCode, String defaultMsg, String locale) {
        return reactiveStringRedisTemplate.opsForValue().get(I18nUtil.buildI18Key(tenantId, "global", errorCode, locale))
                .defaultIfEmpty("")
                .map(i18nValue -> {
                    String errorMsg = StringUtil.isBlank(i18nValue) ? defaultMsg : i18nValue;
                    ErrorInfo errorInfo = new ErrorInfo(errorCode, errorMsg);
                    ActionResult<Void> result = new ActionResult<>();
                    result.addError(errorInfo);
                    return result;
                });
    }

    /**
     * todo this will be integrate into token ingore, or more generally, dynamic configuration
     *
     * @param path
     * @return
     */
    private boolean isUserOperation(RequestPath path) {
        return true;
    }

    public static class Config {
    }
}
