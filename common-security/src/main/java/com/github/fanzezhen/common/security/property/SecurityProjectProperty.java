package com.github.fanzezhen.common.security.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityProjectProperty {
    @Value("${project.code}")
    public String APP_CODE;
    @Value("${project.version}")
    public String VERSION;
    @Value("${project.base.package}")
    public String BASE_PACKAGE;
    @Value("${project.title}")
    public String TITLE;
    @Value("${project.description}")
    public String DESCRIPTION;
    @Value("${project.link.man}")
    public String LINK_MAN;
    @Value("${project.link.url}")
    public String LINK_URL;
    @Value("${project.link.email}")
    public String LINK_EMAIL;
    @Value("${project.license}")
    public String LICENSE;
    @Value("${project.license.url}")
    public String LICENSE_URL;
}
