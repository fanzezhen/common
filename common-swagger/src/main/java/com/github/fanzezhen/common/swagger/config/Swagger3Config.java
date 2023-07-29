package com.github.fanzezhen.common.swagger.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.property.ProjectProperty;
import com.github.fanzezhen.common.core.constant.SysConstant;
import com.github.fanzezhen.common.core.model.bean.SwaggerRequestParameter;
import com.github.fanzezhen.common.swagger.SwaggerProperty;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zezhen.fan
 */
@Configuration
public class Swagger3Config implements WebMvcConfigurer {
    @Resource
    private ProjectProperty projectProperty;
    @Resource
    private SwaggerProperty swaggerProperty;

    private Info info() {
        return new Info()
                .title(projectProperty.getTitle())
                .description(projectProperty.getDescription())
                .contact(new Contact()
                        .name(projectProperty.getLinkMan())
                        .url(projectProperty.getLinkUrl())
                        .email(projectProperty.getLinkEmail())
                )
                .version(projectProperty.getVersion());
    }

    @Bean
    public OpenAPI springOpenApi() {
        new OpenAPI().paths()
        return new OpenAPI().info(info());
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
