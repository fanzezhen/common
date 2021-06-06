package com.github.fanzezhen.common.gateway.core.discover.choose;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author zezhen.fan
 */
public class AlwaysTrueChooser implements Chooser {

	@Override
	public boolean test(List<ServiceInstance> serviceInstances) {
		return true;
	}
}
