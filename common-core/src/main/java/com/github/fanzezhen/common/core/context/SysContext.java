package com.github.fanzezhen.common.core.context;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.constant.SysConstant;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.*;

/**
 * @author zezhen.fan
 */
@Slf4j
public class SysContext {
    private final transient CaseInsensitiveMap<String, String> contextMap = new CaseInsensitiveMap<>(10);

    /**
     * context map 最大容量
     */
    public static final Integer MAX_CAPACITY = 100;
    /**
     * context map key 或者 value 最大值
     */
    public static final Integer MAX_SIZE = 1024;

    public CaseInsensitiveMap<String, String> getContextMap() {
        return contextMap;
    }

    public String get(String key) {
        return contextMap.get(key);
    }

    /**
     * （设置名值对。如果Map之前为null，则会被初始化） Put the key-value into the context map;
     * <p/>
     * Initialize the map if it doesn't exist.
     *
     * @param key   键
     * @param value 值
     * @return 之前的值
     */
    public String set(String key, String value) {
        if (key != null && value != null) {
            if (key.length() > MAX_SIZE) {
                throw new RuntimeException("key is more than " + MAX_SIZE + ", i can't set it into the context map");
            } else if (value.length() > MAX_SIZE) {
                throw new RuntimeException("value is more than " + MAX_SIZE + ", i can't set it into the context map");
            } else {
                Map<String, String> contextMap = this.getContextMap();
                if (contextMap.size() > MAX_CAPACITY) {
                    throw new RuntimeException("the context map is full, can't set anything");
                } else {
                    contextMap.put(key.toLowerCase(), value);
                }
            }
        } else {
            log.error("key:" + key + " or value:" + value + " is null,i can't set it into the context map");
        }
        return value;
    }

    /**
     * 移除一个key
     *
     * @param key key
     */
    public String remove(String key) {
        return this.contextMap.remove(key);
    }

    /**
     * 获取当前登录用户的Id
     *
     * @return userId
     */
    public String getUserId() {
        return get(SysConstant.HEADER_USER_ID);
    }

    public void setUserId(String userId) {
        set(SysConstant.HEADER_USER_ID, userId);
    }

    public String getAccountId() {
        return get(SysConstant.HEADER_ACCOUNT_ID);
    }

    public void setAccountId(String accountId) {
        set(SysConstant.HEADER_ACCOUNT_ID, accountId);
    }

    public String getAccountName() {
        return get(SysConstant.HEADER_ACCOUNT_NAME);
    }

    public void setAccountName(String accountName) {
        set(SysConstant.HEADER_ACCOUNT_NAME, accountName);
    }

    public String getProjectId() {
        return get(SysConstant.HEADER_PROJECT_ID);
    }

    public void setProjectId(String projectId) {
        set(SysConstant.HEADER_PROJECT_ID, projectId);
    }

    public String getAppId() {
        return get(SysConstant.HEADER_APP_CODE);
    }

    public void setAppId(String appId) {
        set(SysConstant.HEADER_APP_CODE, appId);
    }

    public String getCurrentAppCode() {
        return get(SysConstant.CURRENT_PROJECT_ID_KEY);
    }

    public void setCurrentAppCode(String appCode) {
        set(SysConstant.CURRENT_PROJECT_ID_KEY, appCode);
    }

    public String getTraceId() {
        return get(SysConstant.HEADER_TRACE_ID);
    }

    public void setTraceId(String traceId) {
        set(SysConstant.HEADER_TRACE_ID, traceId);
    }

    public String getNodeId() {
        return get(SysConstant.HEADER_NODE_ID);
    }

    public void setNodeId(String nodeId) {
        set(SysConstant.HEADER_NODE_ID, nodeId);
    }

    /**
     * 获取当前登录用户的name
     *
     * @return username
     */
    public String getUserName() {
        return get(SysConstant.HEADER_USER_NAME);
    }

    public void setUserName(String userName) {
        set(SysConstant.HEADER_USER_NAME, userName);
    }

    /**
     * 获取当前登录用户的浏览器信息
     *
     * @return userAgent
     */
    public String getUseAgent() {
        return get(SysConstant.HEADER_USER_AGENT);
    }

    /**
     * 获取当前登录用户所属的租户Id
     *
     * @return tenantId
     */
    public String getTenantId() {
        return get(SysConstant.HEADER_TENANT_ID);
    }

    public void setTenantId(String tenantId) {
        set(SysConstant.HEADER_TENANT_ID, tenantId);
    }

    /**
     * 获取当前登录用户的区域和语言
     * 如果没有设置 返回简体中文(zh_CN)
     *
     * @return locale
     */
    public String getLocale() {
        String locale = get(SysConstant.HEADER_LOCALE);
        if (locale == null || locale.isEmpty()) {
            return SysConstant.DEFAULT_LOCALE;
        }
        return locale;
    }

    public void setLocale(String locale) {
        set(SysConstant.HEADER_LOCALE, locale);
    }

    /**
     * 获取当前登录用户的时区设置
     * 如果没有设置 返回服务器默认时区
     *
     * @return 时区
     */
    public TimeZone getTimeZone() {
        String zoneOffset = get(SysConstant.HEADER_TIME_ZONE);
        if (StrUtil.isBlank(zoneOffset)) {
            return TimeZone.getDefault();
        }
        // 和user profile的timezone的格式匹配
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(Integer.parseInt(zoneOffset) * 3600);
        return TimeZone.getTimeZone(offset);
    }

    public void setTimeZone(String timeZone) {
        set(SysConstant.HEADER_TIME_ZONE, timeZone);
    }

    /**
     * 获取登录用户的IP地址
     *
     * @return clientIp
     */
    public String getClientIp() {
        return get(SysConstant.HEADER_USER_IP);
    }

    public void setClientIp(String clientIp) {
        set(SysConstant.HEADER_USER_IP, clientIp);
    }

    public void setUserAgent(String userAgent) {
        set(SysConstant.HEADER_USER_AGENT, userAgent);
    }

    public String getServerHost() {
        return get(SysConstant.HEADER_SERVER_HOST);
    }

    public void setServerHost(String serverHost) {
        set(SysConstant.HEADER_SERVER_HOST, serverHost);
    }

    public void removeServerHost() {
        this.remove(SysConstant.HEADER_SERVER_HOST);
    }

    public void clean() {
        contextMap.clear();
    }

    public List<Pair<String, String>> toHeaders() {
        if (this.contextMap.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Pair<String, String>> pairs = new ArrayList<>();
            for (Map.Entry<String, String> entry : this.contextMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StrUtil.isBlank(value)) {
                    log.warn("header:" + key + "'s value:" + value + " is empty,will not add to headers");
                } else if (StrUtil.startWithIgnoreCase(key, SysConstant.CONTEXT_HEADER_PREFIX)) {
                    if (log.isDebugEnabled()) {
                        log.debug("adding header{" + key + ":" + value + "}");
                    }

                    if (!StrUtil.equalsIgnoreCase(key, SysConstant.HEADER_ACCOUNT_NAME) && !StrUtil.equalsIgnoreCase(key, SysConstant.HEADER_USER_NAME)) {
                        pairs.add(this.convertKey(key, value, false));
                    } else {
                        pairs.add(this.convertKey(key, value, true));
                    }
                }
            }
            return pairs;
        }
    }

    private Pair<String, String> convertKey(String name, String value, boolean encoding) {
        return encoding ? Pair.of(name, URLEncoder.encode(value, StandardCharsets.UTF_8)) : Pair.of(name, value);
    }
}

