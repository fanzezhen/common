package com.github.fanzezhen.common.gateway.core.tracing;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zezhen.fan
 */
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.gateway.tracing")
public class TracingProperties {

	private Sender sender = new Sender();

	private Reporter reporter = new Reporter();

	private Sampler sampler = new Sampler();

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	public Sampler getSampler() {
		return sampler;
	}

	public void setSampler(Sampler sampler) {
		this.sampler = sampler;
	}

	public static class Sender {
		private String endpoint;

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
	}

	public static class Reporter {
		private boolean useConsole = true;
		private Integer maxQueueSize = 1000;
		private Integer flushIntervalMs = 200;

		public boolean isUseConsole() {
			return useConsole;
		}

		public void setUseConsole(boolean useConsole) {
			this.useConsole = useConsole;
		}

		public Integer getMaxQueueSize() {
			return maxQueueSize;
		}

		public void setMaxQueueSize(Integer maxQueueSize) {
			this.maxQueueSize = maxQueueSize;
		}

		public Integer getFlushIntervalMs() {
			return flushIntervalMs;
		}

		public void setFlushIntervalMs(Integer flushIntervalMs) {
			this.flushIntervalMs = flushIntervalMs;
		}
	}

	public static class Sampler {
		private Number param = 1;

		public Number getParam() {
			return param;
		}

		public void setParam(Number param) {
			this.param = param;
		}
	}

}
