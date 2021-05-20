package com.github.fanzezhen.common.log.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Configuration
@ComponentScan("com.github.fanzezhen.common.log")
@EntityScan({"com.github.fanzezhen.common.log.foundation.entity"})
@MapperScan({"com.github.fanzezhen.common.log.foundation.mapper"})
@EnableFeignClients(basePackages = {"com.github.fanzezhen.common.log"})
public class LogSpringConfig {
}
