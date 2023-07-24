package com.github.fanzezhen.common.gateway.core.discover;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Component
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.gateway.discover.locator")
public class DiscoverLocatorProperties {

	private String routeIdPrefix = "api-";

	/**
	 * SpEL expression that create the uri for each route, defaults to: 'lb://'+serviceId.
	 */
	private String urlExpression = "'lb://'+serviceId";

	private String contextPath = "";
	private String security = "";
	private String intranetHost = "";
	private String intranetUrlPrefix = "/intranet";
	/**
	 * Option to lower case serviceId in predicates and filters, defaults to false. Useful
	 * with eureka when it automatically uppercases serviceId. so MYSERIVCE, would match
	 * /myservice/**
	 */
	private boolean lowerCaseServiceId = true;

	private boolean useEurekaMeta = true;

	public String getRouteIdPrefix() {
		return routeIdPrefix;
	}

	public void setRouteIdPrefix(String routeIdPrefix) {
		this.routeIdPrefix = routeIdPrefix;
	}

	public String getUrlExpression() {
		return urlExpression;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getIntranetHost() {
		return intranetHost;
	}

	public void setIntranetHost(String intranetHost) {
		this.intranetHost = intranetHost;
	}

	public String getIntranetUrlPrefix() {
		return intranetUrlPrefix;
	}

	public void setIntranetUrlPrefix(String intranetUrlPrefix) {
		this.intranetUrlPrefix = intranetUrlPrefix;
	}

	public boolean isUseEurekaMeta() {
		return useEurekaMeta;
	}

	public void setUseEurekaMeta(boolean useEurekaMeta) {
		this.useEurekaMeta = useEurekaMeta;
	}

	public void setUrlExpression(String urlExpression) {
		this.urlExpression = urlExpression;
	}

	public boolean isLowerCaseServiceId() {
		return lowerCaseServiceId;
	}

	public void setLowerCaseServiceId(boolean lowerCaseServiceId) {
		this.lowerCaseServiceId = lowerCaseServiceId;
	}

}
