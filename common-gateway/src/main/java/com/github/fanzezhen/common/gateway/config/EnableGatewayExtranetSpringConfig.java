package com.github.fanzezhen.common.gateway.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zezhen.fan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({GatewaySpringConfig.class, GatewayExtranetSpringConfig.class})
public @interface EnableGatewayExtranetSpringConfig {
}
