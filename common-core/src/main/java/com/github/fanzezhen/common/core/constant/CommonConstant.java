package com.github.fanzezhen.common.core.constant;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommonConstant {
    public static final String DEFAULT_USER_PASSWORD = "111111";    //默认密码
    public static final String SEPARATOR = "/";    //分隔符

    public static final String CODED_FORMAT  = "UTF-8";    //编码格式

    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String MINIMALISM_DATE_DATE_PATTERN = "yyyyMMdd";
    public static final String DEFAULT_DATE_HYPHEN = "-";

    public static final boolean LOGIN_FAILED_RETURN_JSON = true;    // 登录失败时，用来判断是返回json数据还是跳转html页面
    public static final boolean LOGIN_SUCCESS_RETURN_JSON = true;    // 登录失败时，用来判断是返回json数据还是跳转html页面

    public static final String LOGIN_FAILED_MESSAGE = "登录失败！";
    public static final String ADMIN_ADDRESS_PATTERN = "/admin/**";
    public static final String OAUTH_ADDRESS_PATTERN = "/oauth/**";
    //    public static final String TOKEN_KEY = "Admin-Token";
    public static final String TOKEN_KEY = "x-token";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String PERMISSION_PREFIX = "PERMISSION_";
    public static final String PERMISSION_DEFAULT_PID = "0";

    public static final ThreadPoolExecutor SYS_EXECUTOR = new ThreadPoolExecutor(
            10, 20, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }
}
