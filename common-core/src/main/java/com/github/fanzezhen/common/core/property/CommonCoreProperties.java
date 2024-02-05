package com.github.fanzezhen.common.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Collections;
import java.util.Set;

/**
 * @author zezhen.fan
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.core")
public class CommonCoreProperties {
    private Tenant tenant = new Tenant();
    private boolean responseJson = true;
    private String appCode = "demo";
    private String[] resourcesFileSuffixArr = new String[]{".html", ".css", ".js", ".json", ".doc"};
    private String autoWrapResponseIgnoreUrls = "/v2/api-docs/**,/v3/api-docs/**,/swagger**,/webjars/**";
    private CommonThreadPoolProperties threadPool = new CommonThreadPoolProperties();

    @Data
    public static class Tenant {
        boolean enabled = false;
        private Set<String> ignoreTenantTables = Collections.emptySet();
    }
}
