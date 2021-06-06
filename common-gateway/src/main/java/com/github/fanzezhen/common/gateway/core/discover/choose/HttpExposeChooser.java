package com.github.fanzezhen.common.gateway.core.discover.choose;

import com.github.fanzezhen.common.gateway.core.support.BinderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public class HttpExposeChooser implements Chooser {


	private static Logger logger = LoggerFactory.getLogger(HttpExposeChooser.class);

	@Override
	public boolean test(List<ServiceInstance> serviceInstances) {

		if (serviceInstances == null || serviceInstances.size() == 0) {
			return false;
		}

		ServiceInstance serviceInstance = serviceInstances.get(0);
		Map<String, String> metadata = serviceInstance.getMetadata();
		Prop prop = new Prop();
		try {
			BinderUtil.bindEureka("gateway", metadata, prop);
		} catch (Exception ex) {
			logger.error("error binding gateway protocol and expose", ex);
			// reset
			prop = new Prop();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("service id {} {}", serviceInstance.getServiceId(), prop);
		}
		return ("http".equalsIgnoreCase(prop.protocol) && prop.expose);
	}

	public static class Prop {

		private String protocol = "http";

		private boolean expose = true;

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public boolean isExpose() {
			return expose;
		}

		public void setExpose(boolean expose) {
			this.expose = expose;
		}

		@Override
		public String toString() {
			return "Config{" +
					"protocol='" + protocol + '\'' +
					", expose=" + expose +
					'}';
		}
	}
}
