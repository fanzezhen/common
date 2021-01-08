package com.github.fanzezhen.common.security.config.cas;

import com.github.fanzezhen.common.security.annotation.SecurityCasConfig;
import com.github.fanzezhen.common.security.annotation.SecurityConfig;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zezhen.fan
 */
@SecurityCasConfig
@Configuration
@ComponentScan(value = "com.github.fanzezhen.common.security",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = SecurityConfig.class)})
@EnableFeignClients(basePackages = {"com.github.fanzezhen.common.security"})
public class SecurityCasSpringConfig {
}
