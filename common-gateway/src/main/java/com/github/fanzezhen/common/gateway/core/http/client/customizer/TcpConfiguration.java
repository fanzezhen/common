package com.github.fanzezhen.common.gateway.core.http.client.customizer;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author zezhen.fan
 */
public interface TcpConfiguration extends Function<TcpClient, TcpClient> {

	class ConfigConnectionTimeout implements TcpConfiguration {

		HttpClientProperties properties;

		public ConfigConnectionTimeout(HttpClientProperties properties) {
			this.properties = properties;
		}

		@Override
		public TcpClient apply(TcpClient tcpClient) {
			if (properties.getConnectTimeout() != null) {
				tcpClient = tcpClient.option(
						ChannelOption.CONNECT_TIMEOUT_MILLIS,
						properties.getConnectTimeout());
			}
			return tcpClient;
		}
	}

	class ReadTimeout implements TcpConfiguration {

		HttpClientProperties properties;

		public ReadTimeout(HttpClientProperties properties) {
			this.properties = properties;
		}

		@Override
		public TcpClient apply(TcpClient tcpClient) {
			return tcpClient.doOnConnected(conn -> {
				Duration responseTimeout = properties.getResponseTimeout();
				if (responseTimeout == null) {
					responseTimeout = Duration.ofSeconds(60 * 10);//10 minutes
				}
				conn.addHandler(new ReadTimeoutHandler(responseTimeout.getSeconds(), TimeUnit.SECONDS));
			});

		}
	}
}