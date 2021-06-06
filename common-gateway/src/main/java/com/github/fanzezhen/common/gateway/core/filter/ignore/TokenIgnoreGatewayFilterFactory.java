package com.github.fanzezhen.common.gateway.core.filter.ignore;


import com.github.fanzezhen.common.gateway.core.constant.GatewayAttribute;
import com.github.fanzezhen.common.gateway.core.support.BinderUtil;
import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.gateway.support.NameUtils.normalizeFilterFactoryName;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @author zezhen.fan
 */
public class TokenIgnoreGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenIgnoreGatewayFilterFactory.Config> {

	public static final String IGNORE_URL_KEY = "ignoreUrls";

	private static Logger logger = LoggerFactory.getLogger(TokenIgnoreGatewayFilterFactory.class);

	@SuppressWarnings("ConstantConditions")
	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
			String path = requestUrl.getPath();
			boolean toIgnore = false;
			if (StringUtil.contains(config.ignoreUrls, path)) {
				toIgnore = true;
			} else {
				toIgnore = false;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("path {} toIgnore {} ignoreUrl {}", path, toIgnore, config.ignoreUrls);
			}
			exchange.getAttributes().put(GatewayAttribute.IS_URL_TOKEN_IGNORED, toIgnore);
			return chain.filter(exchange);
		});
	}

	@Override
	public Config newConfig() {
		return new Config();
	}

	public static class Config {
		private List<String> ignoreUrls = new ArrayList<>();

		public List<String> getIgnoreUrls() {
			return ignoreUrls;
		}

		public void setIgnoreUrls(List<String> ignoreUrls) {
			this.ignoreUrls = ignoreUrls;
		}
	}

	public static class DefinitionBuilder {

		@Autowired
		private IgnoreProperties ignoreProperties;

		public FilterDefinition build(ServiceInstance instance, String routeId) {
			List<String> allUrls = new ArrayList<>();
			String fromConfig = ignoreProperties.getUrlMap().get(instance.getServiceId());
			if (StringUtil.isNotBlank(fromConfig)) {
				List<String> urlList = Arrays.asList(fromConfig.split(","));
				if (urlList.size() > 0) {
					for (String s : urlList) {
						if (StringUtil.isNotBlank(s)) {
							allUrls.add(s);
						}
					}
				}
			}

			Prop prop = new Prop();
			BinderUtil.bindEureka("gateway.token", instance.getMetadata(), prop);
			if (StringUtil.isNotBlank(prop.ignoreUrls)) {
				List<String> urlList = Arrays.asList(prop.ignoreUrls.split(","));
				if (urlList.size() > 0) {
					for (String s : urlList) {
						if (StringUtil.isNotBlank(s)) {
							allUrls.add(s);
						}
					}
				}
			}

			FilterDefinition tokenIgnore = new FilterDefinition();
			tokenIgnore.setName(normalizeFilterFactoryName(TokenIgnoreGatewayFilterFactory.class));
			tokenIgnore.addArg(TokenIgnoreGatewayFilterFactory.IGNORE_URL_KEY, "'" + String.join(",", allUrls) + "'");
			return tokenIgnore;

		}

		public static class Prop {
			private String ignoreUrls;

			public String getIgnoreUrls() {
				return ignoreUrls;
			}

			public void setIgnoreUrls(String ignoreUrls) {
				this.ignoreUrls = ignoreUrls;
			}
		}
	}
}
