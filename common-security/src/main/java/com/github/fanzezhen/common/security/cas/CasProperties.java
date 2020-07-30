package com.github.fanzezhen.common.security.cas;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CAS的配置参数
 */
@Data
@Component
public class CasProperties {

    @Value("${security.cas.server.host.url}")
    private String serverUrl;

    @Value("${security.cas.server.host.login_url}")
    private String serverLoginUrl;

    @Value("${security.cas.server.host.logout_url}")
    private String serverLogoutUrl;

    @Value("${security.app.url}")
    private String appUrl;

    @Value("${security.app.login.url}")
    private String appLoginUrl;

    @Value("${security.app.logout.url}")
    private String appLogoutUrl;
}
