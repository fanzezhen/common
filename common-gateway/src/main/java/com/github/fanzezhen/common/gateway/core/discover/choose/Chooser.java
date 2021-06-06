package com.github.fanzezhen.common.gateway.core.discover.choose;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.function.Predicate;

/**
 * decide whether a service should be exposed as a route
 * this is replace the #includeExpression functionality in original definition locator
 * @author zezhen.fan
 */
public interface Chooser extends Predicate<List<ServiceInstance>> {
}
