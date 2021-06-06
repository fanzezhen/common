package com.github.fanzezhen.common.gateway.sub.env;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.github.fanzezhen.common.gateway.core.constant.SystemConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class BaseSubEnvPredicate extends AbstractServerPredicate {

	private static Logger logger = LoggerFactory.getLogger(BaseSubEnvPredicate.class);

	public BaseSubEnvPredicate(IRule rule) {
		super(rule);
	}

	public BaseSubEnvPredicate(IRule rule, IClientConfig clientConfig) {
		super(rule, clientConfig);
	}

	@Override
	public boolean apply(PredicateKey input) {
		return input != null
				&& input.getServer() instanceof DiscoveryEnabledServer
				&& predicate((DiscoveryEnabledServer) input.getServer(), (String) input.getLoadBalancerKey());
	}


	String extractRemoteSubEnv(DiscoveryEnabledServer server) {
		String remoteSubEnv = SystemConstant.SUB_ENV_DEFAULT_ENV;
		Map<String, String> remoteMeta = server.getInstanceInfo().getMetadata();
		if (remoteMeta.containsKey(SystemConstant.SUB_ENV_PROPERTY_KEY)) {
			remoteSubEnv = remoteMeta.get(SystemConstant.SUB_ENV_PROPERTY_KEY);
			if (logger.isDebugEnabled()) {
				logger.debug("extract info: server {}'s sub env is {}", server.getInstanceInfo().getId(), remoteSubEnv);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("extract info: server {}' sub env is master by default",server.getInstanceInfo().getId());
			}
		}
		return remoteSubEnv;
	}

	abstract boolean predicate(DiscoveryEnabledServer server, String subEnv);
}
