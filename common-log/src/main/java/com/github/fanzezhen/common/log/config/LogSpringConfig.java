package com.github.fanzezhen.common.log.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.common.log")
@EnableFeignClients(basePackages = {"com.github.fanzezhen.common.log"})
public class LogSpringConfig {
}
