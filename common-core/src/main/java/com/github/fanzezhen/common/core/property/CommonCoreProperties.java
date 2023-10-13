package com.github.fanzezhen.common.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author zezhen.fan
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.core")
public class CommonCoreProperties {
    private boolean tenantEnabled = false;
    private boolean responseJson = true;
    private String[] resourcesFileSuffixArr = new String[]{".html", ".css", ".js", ".json", ".doc"};
    private String autoWrapResponseIgnoreUrls = "/v2/api-docs/**,/v3/api-docs/**,/swagger**,/webjars/**";
    private CommonProjectProperties commonProjectProperties = new CommonProjectProperties();
    private CommonThreadPoolProperties threadPoolProperties = new CommonThreadPoolProperties();

}
