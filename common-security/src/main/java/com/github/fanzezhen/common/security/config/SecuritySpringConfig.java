package com.github.fanzezhen.common.security.config;

import com.github.fanzezhen.common.security.annotation.SecurityCasConfig;
import com.github.fanzezhen.common.security.annotation.SecurityConfig;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author zezhen.fan
 */
@SecurityConfig
@Configuration
@ComponentScan(value = "com.github.fanzezhen.common.security",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = SecurityCasConfig.class)})
@EnableFeignClients(basePackages = {"com.github.fanzezhen.common.security"})
public class SecuritySpringConfig {
}
