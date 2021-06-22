package com.github.fanzezhen.common.gateway.core.http.client.customizer.impl;

import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import com.github.fanzezhen.common.gateway.core.http.client.customizer.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.lang.Nullable;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author zezhen.fan
 */
public interface Metrics {
	final String KEY_TIMER = "timer";

	class OnContext implements DoOnContext {

		MeterRegistry meterRegistry;
		public OnContext(MeterRegistry meterRegistry) {
			this.meterRegistry = meterRegistry;
		}


		@Override
		public Context apply(Context ctx) {
			TimerCtx timerCtx = new TimerCtx(meterRegistry);
			Timer.Sample sample = Timer.start(meterRegistry);
			timerCtx.setSample(sample);
			return ctx.put(KEY_TIMER, timerCtx);
		}
	}

	class OnRequest implements DoOnRequest {

		@Override
		public void accept(HttpClientRequest req, Connection conn) {
			SocketAddress socketAddress = conn.channel().remoteAddress();
			if (socketAddress instanceof InetSocketAddress) {
				String hostName = ((InetSocketAddress) socketAddress).getHostName();
				ContextView ctx = req.currentContextView();
				if (ctx.hasKey(KEY_TIMER)) {
					TimerCtx timerCtx = ctx.get(KEY_TIMER);
					timerCtx.setTags("host", hostName);
				}
			}
		}
	}

	class AfterRequest implements DoAfterRequest {

		Iterable<Tag> defaultTags;

		@Override
		public void accept(HttpClientRequest req, Connection connection) {
			ContextView ctx = req.currentContextView();
			if (ctx.hasKey(KEY_TIMER)) {
				TimerCtx timerCtx = ctx.get(KEY_TIMER);
				HttpHeaders headers = req.requestHeaders();
				String serviceId = headers.get("X-Forwarded-Prefix");
				HttpMethod method = req.method();
				timerCtx.setTags("method", method.name());
				if (StringUtil.isNotBlank(serviceId)) {
					timerCtx.setTags("serviceId", serviceId);
				}
				String host = headers.get("host");
				if (StringUtil.isNotBlank(host)) {
					timerCtx.setTags("target", host);
				}
			}
		}
	}

	class OnRequestError implements DoOnError.Request {

		@Override
		public void accept(HttpClientRequest req, Throwable throwable) {
			ContextView ctx = req.currentContextView();
			if (ctx.hasKey(KEY_TIMER)) {
				TimerCtx timerCtx = ctx.get(KEY_TIMER);
				timerCtx.setTags("status", "reqError");

				Timer.Sample sample = timerCtx.getSample();
				sample.stop(timerCtx.getMeterRegistry().timer("gateway.http.client", timerCtx.getTags()));
			}
		}
	}

	class OnResponseError implements DoOnError.Response {

		@Override
		public void accept(HttpClientResponse res, Throwable throwable) {
			ContextView ctx = res.currentContextView();
			if (ctx.hasKey(KEY_TIMER)) {
				TimerCtx timerCtx = ctx.get(KEY_TIMER);
				timerCtx.setTags("status", "resError");
				Timer.Sample sample = timerCtx.getSample();
				sample.stop(timerCtx.getMeterRegistry().timer("gateway.http.client", timerCtx.getTags()));
			}
		}
	}

	class OnResponse implements DoOnResponse {

		@Override
		public void accept(HttpClientResponse res, Connection connection) {
			ContextView ctx = res.currentContextView();
			if (ctx.hasKey(KEY_TIMER)) {
				TimerCtx timerCtx = ctx.get(KEY_TIMER);
				timerCtx.setTags("status", res.status().code() + "");
				Timer.Sample sample = timerCtx.getSample();
				sample.stop(timerCtx.getMeterRegistry().timer("gateway.http.client", timerCtx.getTags()));
			}
		}
	}

	class TimerCtx {
		MeterRegistry meterRegistry;
		Timer.Sample sample;
		Tags tags;

		public TimerCtx(MeterRegistry meterRegistry) {
			this.meterRegistry = meterRegistry;
		}

		public MeterRegistry getMeterRegistry() {
			return meterRegistry;
		}

		public void setMeterRegistry(MeterRegistry meterRegistry) {
			this.meterRegistry = meterRegistry;
		}

		public Timer.Sample getSample() {
			return sample;
		}

		public void setSample(Timer.Sample sample) {
			this.sample = sample;
		}

		public Tags getTags() {
			return tags;
		}

		public void setTags(@Nullable String... keyValues) {
			if (keyValues == null || keyValues.length == 0) {
				return;
			}
			if (this.tags == null) {
				this.tags = Tags.of(keyValues);
			} else {
				this.tags = this.tags.and(keyValues);
			}

		}
	}
}
