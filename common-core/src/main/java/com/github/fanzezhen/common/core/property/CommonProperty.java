package com.github.fanzezhen.common.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Data
@Component("FzzCommonProperty")
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.core.common")
public class CommonProperty {
    private boolean responseJson = true;
    private String[] resourcesFileSuffixArr = new String[]{".html", ".css", ".js", ".json", ".doc"};
    private String autoWrapResponseIgnoreUrls = "/v2/api-docs/**,/v3/api-docs/**,/swagger**,/webjars/**";
    private String noRepeatedCacheServiceBean = "CommonCacheServiceImpl";

}
