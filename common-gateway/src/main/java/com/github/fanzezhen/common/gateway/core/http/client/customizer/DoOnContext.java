package com.github.fanzezhen.common.gateway.core.http.client.customizer;

import reactor.util.context.Context;

import java.util.function.Function;

/**
 * @author zezhen.fan
 */
public interface DoOnContext extends Function<Context, Context> {


}
