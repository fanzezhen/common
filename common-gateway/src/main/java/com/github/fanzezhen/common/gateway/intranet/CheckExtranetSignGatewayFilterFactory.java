package com.github.fanzezhen.common.gateway.intranet;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.github.fanzezhen.common.gateway.core.constant.SystemConstant;
import com.github.fanzezhen.common.gateway.core.support.I18nUtil;
import com.github.fanzezhen.common.gateway.core.support.SerializeUtils;
import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import com.github.fanzezhen.common.gateway.core.support.response.ActionResult;
import com.github.fanzezhen.common.gateway.core.support.response.ErrorInfo;
import io.opentracing.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import static com.github.fanzezhen.common.gateway.core.constant.GatewayAttribute.OPEN_TRACING_SPAN;

/**
 * @author fanzezhen
 */
public class CheckExtranetSignGatewayFilterFactory extends AbstractGatewayFilterFactory<CheckExtranetSignGatewayFilterFactory.Config> {
    private Environment environment;
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private static Logger logger = LoggerFactory.getLogger(CheckExtranetSignGatewayFilterFactory.class);

    public CheckExtranetSignGatewayFilterFactory(ReactiveStringRedisTemplate reactiveStringRedisTemplate, Environment environment) {
        super(Config.class);
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.environment = environment;
    }

    private static final String NONCE = "nonce";

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey(NONCE)
                    || CollectionUtils.isEmpty(request.getHeaders().get(NONCE))
                    || !request.getHeaders().containsKey("sign")
                    || CollectionUtils.isEmpty(request.getHeaders().get("sign"))) {
                logger.info("empty sign");
                return write420(exchange);
            }
            String signKey = environment.getProperty("sign.key");
            try {
                String nonce = request.getHeaders().get(NONCE).get(0);
                String sign = request.getHeaders().get("sign").get(0);
                String data = nonce + request.getPath() + signKey;
                boolean isSign = SystemConstant.SIGN.verify(data.getBytes(StandardCharsets.UTF_8), sign.getBytes(StandardCharsets.UTF_8));
                if (isSign) {
                    return chain.filter(exchange);
                }
            } catch (Exception ex) {
                logger.error("wtf ", ex);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }


    public Mono<Void> write420(ServerWebExchange exchange) {
        Span span = exchange.getAttribute(OPEN_TRACING_SPAN);
        if (span != null) {
            logger.warn("no tracing span");
            span.log("write420;");
        }
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return exchange.getResponse().writeWith(authFail("", "420", "sign is null or not exists", "")
                .map(action -> exchange.getResponse().bufferFactory().wrap(SerializeUtils.toJsonBytes(action)))
        );
    }

    /**
     * todo move to a different module
     */
    public Mono<Void> write421(String tenantId, ServerWebExchange exchange) {
        Span span = exchange.getAttribute(OPEN_TRACING_SPAN);
        if (span != null) {
            logger.warn("no tracing span");
            span.log("write421;");
        }
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return exchange.getResponse().writeWith(authFail(tenantId, "421", "sign is error", "")
                .map(action -> exchange.getResponse().bufferFactory().wrap(SerializeUtils.toJsonBytes(action)))
        );
    }


    /**
     * todo move to a different module
     */
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


    public static class Config {
    }
}
