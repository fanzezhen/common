package com.github.fanzezhen.common.gateway.core.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zezhen.fan
 */
public class DefaultExcludeMeterFilter implements MeterFilter {

	List<String> toExclude;

	public DefaultExcludeMeterFilter(List<String> toExclude) {
		this.toExclude = toExclude == null ? new ArrayList<>() : toExclude;
	}

	@Override
	public MeterFilterReply accept(Meter.Id id) {
		for (String exclude : toExclude) {
			String name = id.getName();
			if(name.startsWith(exclude)){
				return MeterFilterReply.DENY;
			}
		}
		return MeterFilterReply.NEUTRAL;
	}
}
