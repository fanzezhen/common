package com.github.fanzezhen.common.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Getter
@Component("securityProperty")
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.core.security")
public class SecurityProperty {
    private final String[] ignoringAntMatchers = new String[]{"/test", "/tmp"};
    private final String appUrl = "http://localhost:8080";
    private final String appLoginUrl = "/login";
    private final String appLogoutUrl = "/logout";
    private final String serverUrl = "http://localhost:8080/cas";
    private final String serverLoginUrl = "http://localhost:8080/cas/login";
    private final String serverLogoutUrl = "http://localhost:8080/cas/logout?service=http://localhost:8080/cas/login";
}
