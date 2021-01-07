package com.github.fanzezhen.common.core;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Getter
@Component
@ConfigurationProperties("project")
public class ProjectProperty {
    @Value("${project.code:DEMO}")
    private String appCode;
    @Value("${project.version:1.0}")
    private String version;
    @Value("${project.base.package:com.github.fanzezhen}")
    private String basePackage;
    @Value("${project.title:DEMO}")
    private String title;
    @Value("${project.description:示例}")
    private String description;
    @Value("${project.link.man:fanzezhen}")
    private String linkMan;
    @Value("${project.link.url:https://github.com/fanzezhen/}")
    private String linkUrl;
    @Value("${project.link.email:fanzezhen@outlook.com}")
    private String linkEmail;
    @Value("${project.license:Apache License 2.0}")
    private String license;
    @Value("${project.license.url:http://www.apache.org/licenses/}")
    private String licenseUrl;
}
