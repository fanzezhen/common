package com.github.fanzezhen.common.core.constant;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zezhen.fan
 */
public interface CommonConstant {
    /**
     * 项目目录
     */
    String PROJECT_PATH = System.getProperty("user.dir") + File.separator;
    /**
     * 临时文件夹
     */
    String TMP_FOLDER = "tmp";
    /**
     * 临时文件夹全路径
     */
    String TMP_PATH = PROJECT_PATH + TMP_FOLDER + File.separator;
    /**
     * 默认密码
     */
    String DEFAULT_USER_PASSWORD = "111111";
    /**
     * 路径分隔符
     */
    String SEPARATOR_DIR = "/";
    /**
     * URL分隔符
     */
    String SEPARATOR_URL = "?";
    /**
     * 编码格式
     */
    String CODED_FORMAT = "UTF-8";

    String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String DEFAULT_DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    String MINIMALISM_DATE_DATE_PATTERN = "yyyyMMdd";
    String DEFAULT_DATE_HYPHEN = "-";

    /**
     * 登录失败时，用来判断是返回json数据还是跳转html页面
     */
    boolean LOGIN_FAILED_RETURN_JSON = true;
    /**
     * 登录失败时，用来判断是返回json数据还是跳转html页面
     */
    boolean LOGIN_SUCCESS_RETURN_JSON = true;

    String LOGIN_FAILED_MESSAGE = "登录失败！";
    String ADMIN_ADDRESS_PATTERN = "/admin/**";
    String OAUTH_ADDRESS_PATTERN = "/oauth/**";
    String TOKEN_KEY = "x-token";
    String TOKEN_PREFIX = "Bearer ";
    String PERMISSION_PREFIX = "PERMISSION_";
    String PERMISSION_DEFAULT_PID = "0";

    ThreadPoolExecutor SYS_EXECUTOR = new ThreadPoolExecutor(
            10, 15, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());
}
