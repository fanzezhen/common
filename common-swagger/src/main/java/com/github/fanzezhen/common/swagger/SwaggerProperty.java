package com.github.fanzezhen.common.swagger;

import com.github.fanzezhen.common.core.model.bean.SwaggerRequestParameter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zezhen.fan
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.swagger")
public class SwaggerProperty {
    private List<SwaggerRequestParameter> headerRequestParameterList;
}
