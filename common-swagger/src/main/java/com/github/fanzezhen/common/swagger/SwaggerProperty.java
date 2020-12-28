package com.github.fanzezhen.common.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Component("swaggerProperty")
public class SwaggerProperty {
    @Value("${project.code}")
    public String appCode;
    @Value("${project.version}")
    public String version;
    @Value("${project.base.package}")
    public String basePackage;
    @Value("${project.title}")
    public String title;
    @Value("${project.description}")
    public String description;
    @Value("${project.link.man}")
    public String linkMan;
    @Value("${project.link.url}")
    public String linkUrl;
    @Value("${project.link.email}")
    public String linkEmail;
    @Value("${project.license}")
    public String license;
    @Value("${project.license.url}")
    public String licenseUrl;
}
