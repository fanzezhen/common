package com.github.fanzezhen.common.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.core.common")
public class CommonProperty {
    private final boolean responseJson = true;
    private final String autoWrapResponseIgnoreUrls = "";

}
