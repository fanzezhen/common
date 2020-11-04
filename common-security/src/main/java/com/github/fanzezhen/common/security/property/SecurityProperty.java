package com.github.fanzezhen.common.security.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Component("securityProperty")
public class SecurityProperty {
    @Value("${security.response.json}")
    public boolean responseJsonFlag;
    @Value("${security.ignoring.ant.matchers}")
    public String[] ignoringAntMatchers;
}
