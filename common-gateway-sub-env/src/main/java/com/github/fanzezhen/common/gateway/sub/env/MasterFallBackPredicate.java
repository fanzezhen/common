package com.github.fanzezhen.common.gateway.sub.env;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.github.fanzezhen.common.gateway.core.constant.SystemConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * only matches when remove env is master
 */
public class MasterFallBackPredicate extends BaseSubEnvPredicate {

	private static Logger logger = LoggerFactory.getLogger(MasterFallBackPredicate.class);

	public MasterFallBackPredicate(IRule rule) {
		super(rule);
	}

	public MasterFallBackPredicate(IRule rule, IClientConfig clientConfig) {
		super(rule, clientConfig);
	}

	@Override
	boolean predicate(DiscoveryEnabledServer server, String subEnv) {
		String remoteSubEnv = extractRemoteSubEnv(server);
		boolean matched = SystemConstant.SUB_ENV_DEFAULT_ENV.equalsIgnoreCase(remoteSubEnv);
		if (logger.isDebugEnabled()) {
			logger.debug("server {} is matched {} by default master sub env",  server.getInstanceInfo().getId(), matched);
		}
		return matched;
	}
}
