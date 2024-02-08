package com.github.fanzezhen.common.all;

import com.github.fanzezhen.common.core.config.EnableCoreConfig;
import com.github.fanzezhen.common.exception.config.EnableExceptionConfig;
import com.github.fanzezhen.common.log.config.EnableLogConfig;
import com.github.fanzezhen.common.swagger.config.EnableSpringDocConfig;
import com.github.fanzezhen.common.web.config.EnableWebConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zezhen.fan
 */
@EnableCoreConfig   
@EnableLogConfig
@EnableExceptionConfig
@EnableSpringDocConfig
@EnableWebConfig
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableAllConfig {
}
