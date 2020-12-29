package com.github.fanzezhen.common.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Component("swaggerProperty")
public class SwaggerProperty {
    @Value("${project.code:DEMO}")
    public String appCode;
    @Value("${project.version:1.0}")
    public String version;
    @Value("${project.base.package:com.github.fanzezhen}")
    public String basePackage;
    @Value("${project.title:DEMO}")
    public String title;
    @Value("${project.description:示例}")
    public String description;
    @Value("${project.link.man:fanzezhen}")
    public String linkMan;
    @Value("${project.link.url:https://github.com/fanzezhen/}")
    public String linkUrl;
    @Value("${project.link.email:fanzezhen@outlook.com}")
    public String linkEmail;
    @Value("${project.license:Apache License 2.0}")
    public String license;
    @Value("${project.license.url:http://www.apache.org/licenses/}")
    public String licenseUrl;
}
