package com.github.fanzezhen.common.swagger.config;

import com.github.fanzezhen.common.core.config.EnableCommonCoreConfig;
import org.springframework.context.annotation.Import;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zezhen.fan
 */
@EnableOpenApi
@EnableCommonCoreConfig
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SwaggerSpringConfig.class})
public @interface EnableCommonSwaggerConfig {
}
