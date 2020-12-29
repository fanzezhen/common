package com.github.fanzezhen.common.core.constant;

/**
 * @author zezhen.fan
 */
public interface SysConstant {
    String USER_AGENT = "User-Agent";
    String X_FORWARDED_FOR = "x-forwarded-for";
    String PROXY_CLIENT_IP = "Proxy-Client-IP";
    String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    String DEFAULT_PASSWORD = "111111";
    String TMP_PATH = System.getProperty("user.dir") + "/tmp/";
}
