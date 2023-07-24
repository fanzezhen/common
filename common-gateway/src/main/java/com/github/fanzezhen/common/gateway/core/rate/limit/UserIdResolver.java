package com.github.fanzezhen.common.gateway.core.rate.limit;

import com.github.fanzezhen.common.core.constant.SysConstant;
import com.github.fanzezhen.common.gateway.core.constant.CommonGatewayConstant;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author zezhen.fan
 */
public class UserIdResolver implements KeyResolver {
	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {
		List<String> userIdList = exchange.getRequest().getHeaders().get(SysConstant.HEADER_USER_ID);
		return userIdList == null || userIdList.size()==0 ? Mono.empty() : Mono.just(userIdList.get(0));
	}
}
