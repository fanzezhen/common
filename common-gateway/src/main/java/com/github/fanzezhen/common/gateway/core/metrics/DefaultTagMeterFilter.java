package com.github.fanzezhen.common.gateway.core.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.cloud.commons.util.InetUtils;

/**
 * @author zezhen.fan
 */
public class DefaultTagMeterFilter implements MeterFilter {

	InetUtils.HostInfo hostInfo;

	public DefaultTagMeterFilter(InetUtils.HostInfo hostInfo) {
		this.hostInfo = hostInfo;
	}

	@Override
	public Meter.Id map(Meter.Id id) {
		return id.withTag(Tag.of("selfIp",hostInfo.getIpAddress()));
	}
}
