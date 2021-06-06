package com.github.fanzezhen.common.gateway.core.support;

/**
 * @author zezhen.fan
 */
public class I18nUtil {

    public static String buildI18Key(String tenantId, String appId, String i18nKey, String locale) {
        return "omp:i18n:" + tenantId + "#" + appId + "#" + i18nKey + "#" + locale;
    }
}
