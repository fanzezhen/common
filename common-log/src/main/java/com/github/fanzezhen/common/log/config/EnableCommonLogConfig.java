package com.github.fanzezhen.common.log.config;

import com.github.fanzezhen.common.core.config.EnableCommonCoreConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zezhen.fan
 */
@EnableCommonCoreConfig
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({LogSpringConfig.class})
public @interface EnableCommonLogConfig {
}
