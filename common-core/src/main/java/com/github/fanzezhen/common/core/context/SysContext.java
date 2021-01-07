package com.github.fanzezhen.common.core.context;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.constant.SysConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author zezhen.fan
 */
public class SysContext {
    /**
     * 用于保存线程相关信息
     */
    private final transient static ThreadLocal<Map<String, String>> CONTEXT_MAP = new ThreadLocal<>();

    private static final Logger logger = LoggerFactory.getLogger(SysContext.class);

    /**
     * context map 最大容量
     */
    private static final Integer MAX_CAPACITY = 100;
    /**
     * context map key 或者 value 最大值
     */
    private static final Integer MAX_SIZE = 1024;

    /**
     * 构造函数
     */
    public SysContext() {
        super();
    }

    /**
     * 从 ThreadLocal中获取名值Map(不包含appCode)
     *
     * @return 名值Map
     */
    public static Map<String, String> getContextMap() {
        return CONTEXT_MAP.get();
    }

    /**
     * （获取键下的值.如果不存在，返回null；如果名值Map未初始化，也返回null） Get the value of key. Would
     * return null if context map hasn't been initialized.
     *
     * @param key 键
     * @return 键下的值
     */
    public static String get(String key) {
        Map<String, String> contextMap = getContextMap();
        if (contextMap == null) {
            return null;
        }
        return contextMap.get(convertKey(key));
    }

    /**
     * （设置名值对。如果Map之前为null，则会被初始化） Put the key-value into the context map;
     * <p/>
     * Initialize the map if the it doesn't exist.
     *
     * @param key   键
     * @param value 值
     * @return 之前的值
     */
    public static String put(String key, String value) {
        if (key == null) {
            logger.error("key: is null, can't put it into the context map");
            return value;
        }
        if (key.length() > MAX_SIZE) {
            throw new RuntimeException("key is more than " + MAX_SIZE + ", can't put it into the context map");
        }
        if (value != null && value.length() > MAX_SIZE) {
            throw new RuntimeException("value is more than " + MAX_SIZE + ", can't put it into the context map");
        }
        Map<String, String> contextMap = getContextMap();
        if (contextMap == null) {
            contextMap = new HashMap<>(16);
            SysContext.CONTEXT_MAP.set(contextMap);
        }
        if (contextMap.size() > MAX_CAPACITY) {
            throw new RuntimeException("the context map is full, can't put anything");
        }
        if (value == null) {
            contextMap.remove(convertKey(key));
            return null;
        }
        return contextMap.put(convertKey(key), value);
    }

    /**
     * 移除一个key
     *
     * @param key key
     */
    public static void remove(String key) {
        if (key == null) {
            logger.error("key: is null, can't remove");
            return;
        }
        Map<String, String> CONTEXT_MAP = getContextMap();
        if (CONTEXT_MAP != null) {
            CONTEXT_MAP.remove(convertKey(key));
        }
    }

    private static String convertKey(String key) {
        return key.toLowerCase();
    }

    /**
     * 获取当前登录用户的Id
     *
     * @return userId
     */
    public static String getUserId() {
        return get(SysConstant.HEADER_USER_ID);
    }

    public static void setUserId(String userId) {
        put(SysConstant.HEADER_USER_ID, userId);
    }

    public static String getAccountId() {
        return get(SysConstant.HEADER_ACCOUNT_ID);
    }

    public static void setAccountId(String accountId) {
        put(SysConstant.HEADER_ACCOUNT_ID, accountId);
    }

    public static String getAccountName() {
        return get(SysConstant.HEADER_ACCOUNT_NAME);
    }

    public static void setAccountName(String accountName) {
        put(SysConstant.HEADER_ACCOUNT_NAME, accountName);
    }

    public static String getProjectId() {
        return get(SysConstant.HEADER_PROJECT_ID);
    }

    public static void setProjectId(String projectId) {
        put(SysConstant.HEADER_PROJECT_ID, projectId);
    }

    public static String getAppId() {
        return get(SysConstant.HEADER_APP_CODE);
    }

    public static void setAppId(String appId) {
        put(SysConstant.HEADER_APP_CODE, appId);
    }

    public static String getCurrentAppCode() {
        return get(SysConstant.CURRENT_PROJECT_ID_KEY);
    }

    public static void setCurrentAppCode(String appCode) {
        put(SysConstant.CURRENT_PROJECT_ID_KEY, appCode);
    }

    public static String getTraceId() {
        return get(SysConstant.HEADER_TRACE_ID);
    }

    public static void setTraceId(String traceId) {
        put(SysConstant.HEADER_TRACE_ID, traceId);
    }

    public static String getNodeId() {
        return get(SysConstant.HEADER_NODE_ID);
    }

    public static void setNodeId(String nodeId) {
        put(SysConstant.HEADER_NODE_ID, nodeId);
    }

    /**
     * 获取当前登录用户的name
     *
     * @return username
     */
    public static String getUserName() {
        return get(SysConstant.HEADER_USER_NAME);
    }

    public static void setUserName(String userName) {
        put(SysConstant.HEADER_USER_NAME, userName);
    }

    /**
     * 获取当前登录用户的浏览器信息
     *
     * @return userAgent
     */
    public static String getUseAgent() {
        return get(SysConstant.HEADER_USER_AGENT);
    }

    /**
     * 获取当前登录用户所属的租户Id
     *
     * @return tenantId
     */
    public static String getTenantId() {
        return get(SysConstant.HEADER_TENANT_ID);
    }

    public static void setTenantId(String tenantId) {
        put(SysConstant.HEADER_TENANT_ID, tenantId);
    }

    /**
     * 获取当前登录用户的区域和语言
     * 如果没有设置 返回简体中文(zh_CN)
     *
     * @return locale
     */
    public static String getLocale() {
        String locale = get(SysConstant.HEADER_LOCALE);
        if (locale == null || locale.isEmpty()) {
            return SysConstant.DEFAULT_LOCALE;
        }
        return locale;
    }

    public static void setLocale(String locale) {
        put(SysConstant.HEADER_LOCALE, locale);
    }

    /**
     * 获取当前登录用户的时区设置
     * 如果没有设置 返回服务器默认时区
     *
     * @return 时区
     */
    public static TimeZone getTimeZone() {
        String zoneOffset = get(SysConstant.HEADER_TIME_ZONE);
        if (StrUtil.isBlank(zoneOffset)) {
            return TimeZone.getDefault();
        }
        // 和user profile的timezone的格式匹配
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(Integer.parseInt(zoneOffset) * 3600);
        return TimeZone.getTimeZone(offset);
    }

    public static void setTimeZone(String timeZone) {
        put(SysConstant.HEADER_TIME_ZONE, timeZone);
    }

    /**
     * 获取登录用户的IP地址
     *
     * @return clientIp
     */
    public static String getClientIp() {
        return get(SysConstant.HEADER_USER_IP);
    }

    public static void setClientIp(String clientIp) {
        put(SysConstant.HEADER_USER_IP, clientIp);
    }

    public static void setUserAgent(String userAgent) {
        put(SysConstant.HEADER_USER_AGENT, userAgent);
    }

    public static String getServerHost() {
        return get(SysConstant.HEADER_SERVER_HOST);
    }

    public static void setServerHost(String serverHost) {
        put(SysConstant.HEADER_SERVER_HOST, serverHost);
    }

    public static void removeServerHost() {
        getContextMap().remove(SysConstant.HEADER_SERVER_HOST);
    }

    public static void clean() {
        CONTEXT_MAP.remove();
    }

    /**
     * 清除指定的Key对应的元素
     *
     * @param key key
     */
    public static void clean(String key) {
        if (key == null) {
            logger.error("key is null,can't remove");
            return;
        }
        Map<String, String> map = CONTEXT_MAP.get();
        if (map != null) {
            map.remove(convertKey(key));
        }
    }
}

