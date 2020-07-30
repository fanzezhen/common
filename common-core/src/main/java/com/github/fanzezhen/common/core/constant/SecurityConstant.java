package com.github.fanzezhen.common.core.constant;

public class SecurityConstant {

    public static final String HOME_ADDRESS = "/hello";
    public static final String LOGIN_ADDRESS = "/oauth/login";
    public static final String DEFAULT_LOGIN_API = "/login";
    public static final String LOGIN_API = "/user/login";

    // 权限加载的前缀
    public static final String PERMISSION_PREFIX = "permission_";

    // 角色加载的前缀
    public static final String ROLE_PREFIX = "ROLE_";

    // 验证码
    public final static String SESSION_KEY_CAPTCHA = "SESSION_KEY_CAPTCHA";

    // 用户对应在session中key
    public static final String USER_SESSION_KEY = "SECURITY_USER_ATTR";

    // 角色对应在session中的key
    public static final String ROLE_SESSION_KEY = "SECURITY_ROLE_ATTR";

    // 权限对应在session中的key
    public static final String PERMISSION_SESSION_KEY = "SECURITY_PERMISSION_ATTR";

    // 不校验权限的接口
    public static final String[] IGNORING_ANT_MATCHERS = {LOGIN_ADDRESS, "/assets/**", "/kaptcha/**", "/log/**", "/oauth/**",
            "/static/**", "/v2/**", "/swagger-ui.html/**", "/swagger-resources/**", "/webjars/**", "/public/**"};
    // 不校验权限的接口
    public static final String[] CSRF_IGNORING_ANT_MATCHERS = {"/api/**", "/log/**"};
}
