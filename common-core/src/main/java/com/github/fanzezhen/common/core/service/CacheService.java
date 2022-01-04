package com.github.fanzezhen.common.core.service;

/**
 * @author zezhen.fan
 */
public interface CacheService {
    /**
     * 查询并保存
     *
     * @param k                  key
     * @return value
     */
    String get(String k);

    /**
     * 保存
     *
     * @param k       key
     * @param v       value
     * @param timeout 过期时间（毫秒）
     */
    void put(String k, String v, long timeout);
    /**
     * 删除
     *
     * @param k       key
     */
    void remove(String k);
}
