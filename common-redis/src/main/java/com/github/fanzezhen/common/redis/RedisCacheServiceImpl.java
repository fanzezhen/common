package com.github.fanzezhen.common.redis;

import com.github.fanzezhen.common.core.service.CacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zezhen.fan
 */
@Service("FzzRedisCacheServiceImpl")
@ConditionalOnProperty(prefix = "com.github.fanzezhen.common.redis", name = "enabled", havingValue = "true")
public class RedisCacheServiceImpl implements CacheService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String get(String k) {
        return stringRedisTemplate.opsForValue().get(k);
    }

    @Override
    public void put(String k, String v, long timeout) {
        stringRedisTemplate.opsForValue().set(k, v, timeout, TimeUnit.MICROSECONDS);
    }

    @Override
    public void remove(String k) {
        stringRedisTemplate.delete(k);
    }
}
