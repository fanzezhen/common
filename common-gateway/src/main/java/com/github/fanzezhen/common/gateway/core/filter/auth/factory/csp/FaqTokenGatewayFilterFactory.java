package com.github.fanzezhen.common.gateway.core.filter.auth.factory.csp;


import com.github.fanzezhen.common.gateway.core.constant.GatewayAttribute;
import com.github.fanzezhen.common.gateway.core.support.I18nUtil;
import com.github.fanzezhen.common.gateway.core.support.SerializeUtils;
import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import com.github.fanzezhen.common.gateway.core.support.response.ActionResult;
import com.github.fanzezhen.common.gateway.core.support.response.ErrorInfo;
import org.apache.commons.lang.StringUtils;
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
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * @author zezhen.fan
 */
public class FaqTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<FaqTokenGatewayFilterFactory.Config> {

	private static Logger logger = LoggerFactory.getLogger("com.github.fanzezhen.common.gateway.FaqTokenGatewayFilterFactory");

	private ReactiveStringRedisTemplate reactiveStringRedisTemplate;


	public FaqTokenGatewayFilterFactory(ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
		super(Config.class);
		this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
	}


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
			Optional<String> tokenP = CspTokenTransferMode.cookie.getToken(request);
			String token = getCookieValue(request, null);
			if (StringUtil.isBlank(token)) {
				logger.info("empty token");
				return write405(exchange);
			}
			String redisTokenKey = "faq:sso:token:" + token;
			try {
				Mono<String> userInfoMono = reactiveStringRedisTemplate.opsForValue().get(redisTokenKey);
				return userInfoMono.defaultIfEmpty("")
						.onErrorResume(throwable -> {
							logger.error("error request sso session", throwable);
							return Mono.just("");
						})
						.flatMap(tokenJson -> {
							if (StringUtils.isBlank(tokenJson)) {
								logger.info("unable to retrieve session info");
								return write405(exchange);
							}
							return chain.filter(exchange);
						});
			} catch (Exception ex) {
				logger.error("wtf ", ex);
			}
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		};
	}

	public Mono<Void> write405(ServerWebExchange exchange) {
		exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		return exchange.getResponse().writeWith(authFail("", "405", "token is null or not exists", "")
				.map(action -> exchange.getResponse().bufferFactory().wrap(SerializeUtils.toJsonBytes(action)))
		);
	}

	public Mono<Void> write406(String tenantId, ServerWebExchange exchange) {
		exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		return exchange.getResponse().writeWith(authFail(tenantId, "406", "current account has been logged in elsewhere", "")
				.map(action -> exchange.getResponse().bufferFactory().wrap(SerializeUtils.toJsonBytes(action)))
		);
	}



	private String getCookieValue(ServerHttpRequest request, String locale) {
		List<HttpCookie> tokens = request.getCookies().get("token");
		return CollectionUtils.isEmpty(tokens) ? null : tokens.get(0).getValue();
	}

	private String getHeader(ServerHttpRequest request, String environmentToken) {
		List<String> tokens = request.getHeaders().get(environmentToken);
		if (tokens != null && tokens.size() > 1) {
			logger.warn("found more than one tokens: {}", tokens.size());
		}
		return CollectionUtils.isEmpty(tokens) ? null : tokens.get(0);
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
