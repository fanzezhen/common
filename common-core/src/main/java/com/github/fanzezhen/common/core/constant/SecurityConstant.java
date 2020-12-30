package com.github.fanzezhen.common.core.constant;

import cn.hutool.core.util.ArrayUtil;

import java.util.Arrays;

/**
 * @author zezhen.fan
 */
public interface SecurityConstant {
    String HOME_ADDRESS = "/hello";
    String LOGIN_ADDRESS = "/oauth/login";
    String DEFAULT_LOGIN_API = "/login";
    /**
     * 异常接口匹配格式
     */
    String ERROR_MATCHERS = "/error/**";
    /**
     * 日志接口匹配格式
     */
    String LOG_MATCHERS = "/log/**";
    /**
     * 资源接口匹配格式
     */
    String OAUTH_MATCHERS = "/oauth/**";
    /**
     * 开放接口匹配格式
     */
    String PUBLIC_MATCHERS = "/public/**";
    /**
     * 静态文件接口匹配格式
     */
    String STATIC_MATCHERS = "/static/**";
    /**
     * SWAGGER-UI接口匹配格式
     */
    String SWAGGER_UI_MATCHERS = "/swagger-ui/**";
    /**
     * SWAGGER-RESOURCE接口匹配格式
     */
    String SWAGGER_RESOURCE_MATCHERS = "/swagger-resources/**";
    /**
     * SWAGGER-API-DOC接口匹配格式
     */
    String SWAGGER_API_DOCS_MATCHERS = "/v3/api-docs/**";
    /**
     * SWAGGER接口匹配格式集合
     */
    String[] SWAGGER_MATCHERS = {SWAGGER_UI_MATCHERS, SWAGGER_RESOURCE_MATCHERS, SWAGGER_API_DOCS_MATCHERS};

    /**
     * 权限加载的前缀
     */
    String PERMISSION_PREFIX = "permission_";

    /**
     * 角色加载的前缀
     */
    String ROLE_PREFIX = "ROLE_";

    /**
     * 验证码
     */
    String SESSION_KEY_CAPTCHA = "SESSION_KEY_CAPTCHA";

    /**
     * 用户对应在session中key
     */
    String USER_SESSION_KEY = "SECURITY_USER_ATTR";

    /**
     * 角色对应在session中的key
     */
    String ROLE_SESSION_KEY = "SECURITY_ROLE_ATTR";

    /**
     * 权限对应在session中的key
     */
    String PERMISSION_SESSION_KEY = "SECURITY_PERMISSION_ATTR";

    /**
     * 不校验权限的接口
     */
    String[] IGNORING_ANT_MATCHERS = ArrayUtil.addAll(SWAGGER_MATCHERS,
            new String[]{ERROR_MATCHERS, LOGIN_ADDRESS, LOG_MATCHERS, OAUTH_MATCHERS, PUBLIC_MATCHERS, STATIC_MATCHERS});

    /**
     * 忽略CSRF的接口
     */
    String[] CSRF_IGNORING_ANT_MATCHERS = {"/api/**", LOG_MATCHERS, PUBLIC_MATCHERS};
}
