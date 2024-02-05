package com.github.fanzezhen.common.swagger.config;

import com.github.fanzezhen.common.core.property.CommonCoreProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Configuration
public class CommonSwaggerConfig {
    @Resource
    private CommonCoreProperties commonCoreProperties;

    private Info info() {
        return new Info();
    }

    @Bean
    public OpenAPI springOpenApi() {
        return (new OpenAPI()).info(this.info());
    }
}
