package com.github.fanzezhen.common.gateway.core.filter.auth;

import com.github.fanzezhen.common.gateway.core.filter.auth.factory.csp.CspTokenTransferMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zezhen.fan
 */
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.gateway.auth")
public class AuthProperties {

	private CspTokenTransferMode cspTokenTransferMode = CspTokenTransferMode.misc;

	private int expireTimeInSeconds = 3600;

	private int expireTimeAppInSeconds = 3600 * 24 * 30;

	public CspTokenTransferMode getCspTokenTransferMode() {
		return cspTokenTransferMode;
	}

	public void setCspTokenTransferMode(CspTokenTransferMode cspTokenTransferMode) {
		this.cspTokenTransferMode = cspTokenTransferMode;
	}

	public int getExpireTimeInSeconds() {
		return expireTimeInSeconds;
	}

	public void setExpireTimeInSeconds(int expireTimeInSeconds) {
		this.expireTimeInSeconds = expireTimeInSeconds;
	}

	public int getExpireTimeAppInSeconds() {
		return expireTimeAppInSeconds;
	}

	public void setExpireTimeAppInSeconds(int expireTimeAppInSeconds) {
		this.expireTimeAppInSeconds = expireTimeAppInSeconds;
	}
}
