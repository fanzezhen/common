package com.github.fanzezhen.common.redis;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.common.core.service.LockService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * @author zezhen.fan
 */
@Component
@Slf4j
public class RedisLockServiceImpl implements LockService {

    @Resource
    RedissonClient redissonClient;

    @Override
    public <T> T lockKey(String key, long waitTime, TimeUnit timeUnit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean isLock = lock.tryLock(waitTime, timeUnit);
            if (isLock) {
                try {
                    return supplier.get();
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                log.info("没有获取到锁，等待时间结束:{}", key);
            }
        } catch (InterruptedException e) {
            log.info("redis锁错误:{},{}", e, key);
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), "redis锁错误" + key + e.getLocalizedMessage());
        }
        throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), "没有获取到锁，等待时间结束" + key + "：" + waitTime + "：" + timeUnit);
    }


}
