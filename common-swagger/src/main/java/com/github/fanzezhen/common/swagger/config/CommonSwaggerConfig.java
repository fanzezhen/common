package com.github.fanzezhen.common.swagger.config;

import com.github.fanzezhen.common.core.property.CommonCoreProperties;
import com.github.fanzezhen.common.core.property.CommonProjectProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
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
        CommonProjectProperties commonProjectProperties = commonCoreProperties.getCommonProjectProperties();
        return new Info()
                .title(commonProjectProperties.getTitle())
                .description(commonProjectProperties.getDescription())
                .contact(new Contact().name(commonProjectProperties.getLinkMan()).url(commonProjectProperties.getLinkUrl()).email(commonProjectProperties.getLinkEmail()))
                .version(commonProjectProperties.getVersion());
    }

    @Bean
    public OpenAPI springOpenApi() {
        return (new OpenAPI()).info(this.info());
    }
}
