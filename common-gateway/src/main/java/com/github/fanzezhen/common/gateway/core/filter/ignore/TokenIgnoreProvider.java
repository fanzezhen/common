package com.github.fanzezhen.common.gateway.core.filter.ignore;

import java.util.List;

/**
 * @author zezhen.fan
 */
public interface TokenIgnoreProvider {
    /**
     * 获取IgnoredUrlList
     *
     * @param serviceId 微服务ID
     * @return IgnoredUrlList
     */
    List<String> getIgnoredUrls(String serviceId);
}
