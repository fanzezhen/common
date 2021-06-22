package com.github.fanzezhen.common.gateway.core.filter.ignore;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zezhen.fan
 */
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.gateway.ignore")
public class IgnoreProperties {

	Map<String,String> urlMap = new HashMap<>();

	public Map<String, String> getUrlMap() {
		return urlMap;
	}

	public void setUrlMap(Map<String, String> urlMap) {
		this.urlMap = urlMap;
	}
}
