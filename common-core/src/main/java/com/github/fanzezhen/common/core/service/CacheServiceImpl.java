package com.github.fanzezhen.common.core.service;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.common.core.constant.CacheConstant;
import com.github.fanzezhen.common.core.util.ExceptionUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author zezhen.fan
 */
@Order
@Service("CommonCacheServiceImpl")
public class CacheServiceImpl implements CacheService {
    @Override
    public String get(String k) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        return timedCache.get(k, false);
    }

    @Override
    public void put(String k, String v, long timeoutMillis) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.put(k, String.valueOf(System.currentTimeMillis()), timeoutMillis);
    }

    @Override
    public void remove(String k) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        timedCache.remove(k);
    }

    @Override
    public void set(String k, String v, long timeout, TimeUnit timeUnit) {
        TimedCache<String, String> timedCache = CacheConstant.getHourTimedCacheInstance();
        long timeoutMillis = timeout;
        switch (timeUnit) {
            case NANOSECONDS -> ExceptionUtil.throwException("不支持 NANOSECONDS");
            case MICROSECONDS -> ExceptionUtil.throwException("不支持 MICROSECONDS");
            case MILLISECONDS -> timeoutMillis = timeout;
            case SECONDS -> timeoutMillis = timeout * 1000;
            case MINUTES -> timeoutMillis = timeout * 60 * 1000;
            case HOURS -> timeoutMillis = timeout * 60 * 60 * 1000;
            case DAYS -> timeoutMillis = timeout * 12 * 60 * 60 * 1000;
            default -> {
            }
        }
        timedCache.put(k, String.valueOf(System.currentTimeMillis()), timeoutMillis);
    }

}
