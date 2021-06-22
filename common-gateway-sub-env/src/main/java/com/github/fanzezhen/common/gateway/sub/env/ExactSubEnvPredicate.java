package com.github.fanzezhen.common.gateway.sub.env;

import com.netflix.loadbalancer.IRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.github.fanzezhen.common.gateway.core.constant.SystemConstant;
import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * match only when self and remote have the same subEnv,
 * return true when the subEnv is master
 * @author zezhen.fan
 */
public class ExactSubEnvPredicate extends BaseSubEnvPredicate {

	private static Logger logger = LoggerFactory.getLogger(ExactSubEnvPredicate.class);
	private String configTargetSubEnv = SystemConstant.SUB_ENV_DEFAULT_ENV;

	public ExactSubEnvPredicate(IRule rule) {
		super(rule);
	}


	@Override
	boolean predicate(DiscoveryEnabledServer server, String targetSubEnv) {
		String remoteSubEnv = extractRemoteSubEnv(server);
		if (StringUtil.isBlank(targetSubEnv)) {
			if(logger.isDebugEnabled()){
				logger.debug("target sub env is empty. use master by default");
			}
			targetSubEnv = configTargetSubEnv;
		}
		boolean matched = targetSubEnv.equalsIgnoreCase(remoteSubEnv);
		if (logger.isDebugEnabled()) {
			logger.debug("server {} {} matched for sub env {}", server.getInstanceInfo().getId(), matched ? "is" : "is not", targetSubEnv);
		}
		return matched;
	}
}
