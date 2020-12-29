package com.github.fanzezhen.common.swagger;

import com.github.fanzezhen.common.core.constant.CommonConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author zezhen.fan
 */
@EnableOpenApi
@Configuration
public class Swagger2Config implements WebMvcConfigurer {
    @Resource
    private SwaggerProperty swaggerProperty;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping(CommonConstant.SEPARATOR_DIR)
                // 是否开启swagger配置，生产环境需关闭
                .enable(true)
                .apiInfo(this.apiInfo())
                .select() // 指定需要发布到Swagger的接口目录，不支持通配符
                .apis(RequestHandlerSelectors.basePackage(swaggerProperty.basePackage))
                .paths(PathSelectors.any())
                .build()
                // 支持的通讯协议集合
                .protocols(Set.of("https", "http"));
    }

    /**
     * 项目信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(swaggerProperty.title)
                .description(swaggerProperty.description)
                .contact(new Contact(swaggerProperty.linkMan, swaggerProperty.linkUrl, swaggerProperty.linkEmail))
                .version(swaggerProperty.version)
                .license(swaggerProperty.license)
                .licenseUrl(swaggerProperty.licenseUrl)
                .build();
    }
}
