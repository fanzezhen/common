package com.github.fanzezhen.common.gateway.sub.env;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateBasedRule;
import com.netflix.loadbalancer.ZoneAvoidancePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zezhen.fan
 */
public class SubEnvRule extends PredicateBasedRule {

	private static Logger logger = LoggerFactory.getLogger(SubEnvRule.class);

	private CompoPredicate compositePredicate;
	private String name;

	public SubEnvRule() {
		super();
		compositePredicate = createCompositePredicate(null);
	}

	private CompoPredicate createCompositePredicate(IClientConfig clientConfig) {
		ZoneAvoidancePredicate zonePredicate = new ZoneAvoidancePredicate(this, clientConfig);
		ExactSubEnvPredicate exactSubEnvPredicate = new ExactSubEnvPredicate(this);
		CompoPredicate.Builder builder = CompoPredicate.withPredicates(clientConfig == null ? "" : clientConfig.getClientName(), zonePredicate, exactSubEnvPredicate);
		MasterFallBackPredicate masterFallBackPredicate = new MasterFallBackPredicate(this);
		builder
				.addFallbackPredicate(masterFallBackPredicate);
		return builder.build();
	}

	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {
		super.initWithNiwsConfig(clientConfig);
		logger.info("create rule for {}", clientConfig.getClientName());
		this.name = clientConfig.getClientName();
		compositePredicate = createCompositePredicate(clientConfig);
	}

	@Override
	public AbstractServerPredicate getPredicate() {
		return compositePredicate;
	}

	@Override
	public String toString() {
		return "SubEnvRule{" +
				"name='" + name + '\'' +
				'}';
	}
}
