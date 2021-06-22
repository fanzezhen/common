package com.github.fanzezhen.common.gateway.sub.env;

import com.github.fanzezhen.common.gateway.core.constant.SystemConstant;

/**
 * @author zezhen.fan
 */
public class LbKey {

	private String subEnv = SystemConstant.SUB_ENV_DEFAULT_ENV;
	private boolean strictMode = true;

	public LbKey(String subEnv, boolean strictMode) {
		this.subEnv = subEnv;
		this.strictMode = strictMode;
	}

	public String getSubEnv() {
		return subEnv;
	}

	public void setSubEnv(String subEnv) {
		this.subEnv = subEnv;
	}

	public boolean isStrictMode() {
		return strictMode;
	}

	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	@Override
	public String toString() {
		return "LBKey{" +
				"subEnv='" + subEnv + '\'' +
				", strictMode=" + strictMode +
				'}';
	}
}
