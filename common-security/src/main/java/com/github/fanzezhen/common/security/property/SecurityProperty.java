package com.github.fanzezhen.common.security.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Getter
@Component("securityProperty")
@ConfigurationProperties("security")
public class SecurityProperty {

    @Value("${security.ignoring.ant.matchers:/test, /tmp}")
    private String[] ignoringAntMatchers;

    @Value("${security.app.url:http://localhost:8080}")
    private String appUrl;

    @Value("${security.app.login.url:/login}")
    private String appLoginUrl;

    @Value("${security.app.logout.url:/logout}")
    private String appLogoutUrl;

    @Value("${security.cas.server.host.url:http://localhost:8080/cas}")
    private String serverUrl;

    @Value("${security.cas.server.host.login_url:http://localhost:8080/cas/login}")
    private String serverLoginUrl;

    @Value("${security.cas.server.host.logout_url:http://localhost:8080/cas/logout?service=http://localhost:8080/cas/login}")
    private String serverLogoutUrl;
}
