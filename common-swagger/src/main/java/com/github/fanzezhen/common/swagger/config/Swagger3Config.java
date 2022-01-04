package com.github.fanzezhen.common.swagger.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.property.ProjectProperty;
import com.github.fanzezhen.common.core.constant.SysConstant;
import com.github.fanzezhen.common.core.model.bean.SwaggerRequestParameter;
import com.github.fanzezhen.common.swagger.SwaggerProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zezhen.fan
 */
@EnableOpenApi
@Configuration
public class Swagger3Config implements WebMvcConfigurer {
    @Resource
    private ProjectProperty projectProperty;
    @Resource
    private SwaggerProperty swaggerProperty;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .pathMapping(StrUtil.SLASH)
                // 是否开启swagger配置，生产环境需关闭
                .enable(true)
                .apiInfo(this.apiInfo())
                // 指定需要发布到Swagger的接口目录，不支持通配符
                .select()
                .apis(RequestHandlerSelectors.basePackage(projectProperty.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                // 全局请求参数
                .globalRequestParameters(getHeaderGlobalRequestParameters())
                // 支持的通讯协议集合
                .protocols(Set.of("https", "http"))
                ;
    }

    /**
     * 项目信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(projectProperty.getTitle())
                .description(projectProperty.getDescription())
                .contact(new Contact(
                        projectProperty.getLinkMan(), projectProperty.getLinkUrl(), projectProperty.getLinkEmail()))
                .version(projectProperty.getVersion())
                .license(projectProperty.getLicense())
                .licenseUrl(projectProperty.getLicenseUrl())
                .build();
    }

    private List<RequestParameter> getHeaderGlobalRequestParameters() {
        List<SwaggerRequestParameter> headerList = swaggerProperty.getHeaderRequestParameterList();
        if (CollUtil.isEmpty(headerList)) {
            return List.of(
                    new RequestParameterBuilder()
                            .name(SysConstant.HEADER_CLIENT_ID)
                            .in(ParameterType.HEADER)
                            .description("客户端ID")
                            .required(false)
                            .build(),
                    new RequestParameterBuilder()
                            .name(SysConstant.HEADER_PLATFORM)
                            .in(ParameterType.HEADER)
                            .description("平台信息")
                            .required(false)
                            .build(),
                    new RequestParameterBuilder()
                            .name(SysConstant.HEADER_TENANT_ID)
                            .in(ParameterType.HEADER)
                            .description("租户ID")
                            .required(false)
                            .build(),
                    new RequestParameterBuilder()
                            .name(SysConstant.HEADER_TOKEN_KEY)
                            .in(ParameterType.HEADER)
                            .description("身份令牌")
                            .required(false)
                            .build()
            );
        }
        return headerList.stream().map(requestParameter -> new RequestParameterBuilder()
                .name(requestParameter.getName())
                .in(ParameterType.HEADER)
                .description(requestParameter.getDescription())
                .required(requestParameter.getRequired())
                .deprecated(requestParameter.getDeprecated())
                .hidden(requestParameter.getHidden())
                .precedence(requestParameter.getPrecedence())
                .parameterIndex(requestParameter.getParameterIndex())
                .build()).collect(Collectors.toList());
    }
}
