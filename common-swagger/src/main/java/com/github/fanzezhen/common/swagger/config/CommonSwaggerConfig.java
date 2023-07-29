package com.github.fanzezhen.common.swagger.config;

import com.github.fanzezhen.common.core.property.ProjectProperty;
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
    private ProjectProperty projectProperty;

    public CommonSwaggerConfig() {
    }

    private Info info() {
        return new Info()
                .title(this.projectProperty.getTitle())
                .description(this.projectProperty.getDescription())
                .contact(new Contact().name(this.projectProperty.getLinkMan()).url(this.projectProperty.getLinkUrl()).email(this.projectProperty.getLinkEmail()))
                .version(this.projectProperty.getVersion());
    }

    @Bean
    public OpenAPI springOpenApi() {
        return (new OpenAPI()).info(this.info());
    }
}
