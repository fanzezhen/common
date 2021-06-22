package com.github.fanzezhen.common.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.core.project")
public class ProjectProperty {
    private final String appCode = "DEMO";
    private final String version = "1.0";
    private final String basePackage = "com.github.fanzezhen.common";
    private final String dtoPackages = "";
    private final String title = "DEMO";
    private final String description = "示例";
    private final String linkMan = "";
    private final String linkUrl = "";
    private final String linkEmail = "";
    private final String license = "Apache License 2.0";
    private final String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0";
}
