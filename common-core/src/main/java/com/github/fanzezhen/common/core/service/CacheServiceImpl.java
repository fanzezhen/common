package com.github.fanzezhen.common.core.service;

import cn.hutool.cache.impl.TimedCache;
import com.github.fanzezhen.common.core.constant.CacheConstant;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author zezhen.fan
 */
@Service("FzzCacheServiceImpl")
public class CacheServiceImpl implements CacheService {
    @Override
    public String get(String k) {
        TimedCache<String, String> timedCache = CacheConstant.getTimedCacheInstance();
        return timedCache.get(k, false);
    }

    @Override
    public void put(String k, String v, long timeout) {
        TimedCache<String, String> timedCache = CacheConstant.getTimedCacheInstance();
        timedCache.put(k, String.valueOf(System.currentTimeMillis()), timeout);
    }

    @Override
    public void remove(String k) {
        TimedCache<String, String> timedCache = CacheConstant.getTimedCacheInstance();
        timedCache.remove(k);
    }
}
