package com.github.fanzezhen.common.core.thread;

import com.github.fanzezhen.common.core.property.CommonThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zezhen.fan
 * @date 2023/8/14
 */
@Slf4j
public class PoolExecutors {
    private static final Map<String, CommonThreadPoolExecutor> poolExecutorMap = new ConcurrentHashMap<>(8);
    private static final Lock lock = new ReentrantLock();
    private static final CommonThreadPoolProperties threadPoolProperties = new CommonThreadPoolProperties();

    public PoolExecutors() {
    }

    public static CommonThreadPoolExecutor newThreadPool(String name) {
        return newThreadPool(name, threadPoolProperties.getCoreSize());
    }

    public static CommonThreadPoolExecutor newThreadPool(String name, int coreSize) {
        return newThreadPool(name, coreSize, coreSize);
    }

    public static CommonThreadPoolExecutor newThreadPool(String name, int coreSize, int maxSize) {
        return newThreadPool(name, coreSize, maxSize, threadPoolProperties.getQueueCapacity());
    }

    public static CommonThreadPoolExecutor newThreadPool(String name, int coreSize, int maxSize, int queueCapacity) {
        return newThreadPool(name, coreSize, maxSize, queueCapacity, threadPoolProperties.getKeepAliveSeconds());
    }

    public static CommonThreadPoolExecutor newThreadPool(String name, int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds) {
        if (!poolExecutorMap.containsKey(name)) {
            try {
                lock.lock();
                CommonThreadPoolExecutor threadPoolExecutor = createThreadPool(name, coreSize, maxSize, queueCapacity, keepAliveSeconds);
                poolExecutorMap.put(name, threadPoolExecutor);
            } catch (Throwable throwable) {
                log.error("newThreadPool failed", throwable);
            } finally {
                lock.unlock();
            }
        }

        return poolExecutorMap.get(name);
    }

    public static CommonThreadPoolExecutor newThreadPool(String name, int nThreads, ThreadFactory threadFactory) {
        if (!poolExecutorMap.containsKey(name)) {
            try {
                lock.lock();
                CommonThreadPoolExecutor threadPoolExecutor = createThreadPool(name, nThreads, threadFactory);
                poolExecutorMap.put(name, threadPoolExecutor);
            } catch (Throwable throwable) {
                log.error("newThreadPool failed", throwable);
            } finally {
                lock.unlock();
            }
        }

        return poolExecutorMap.get(name);
    }

    private static CommonThreadPoolExecutor createThreadPool(String name, int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds) {
        CommonThreadPoolExecutor threadPoolExecutor = new CommonThreadPoolExecutor();
        threadPoolExecutor.setCorePoolSize(coreSize);
        threadPoolExecutor.setMaxPoolSize(maxSize);
        threadPoolExecutor.setQueueCapacity(queueCapacity);
        threadPoolExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolExecutor.setThreadGroupName(name);
        threadPoolExecutor.afterPropertiesSet();
        return threadPoolExecutor;
    }

    private static CommonThreadPoolExecutor createThreadPool(String name, int coreSize, ThreadFactory threadFactory) {
        CommonThreadPoolExecutor threadPoolExecutor = new CommonThreadPoolExecutor();
        threadPoolExecutor.setCorePoolSize(coreSize);
        threadPoolExecutor.setThreadFactory(threadFactory);
        threadPoolExecutor.afterPropertiesSet();
        threadPoolExecutor.setThreadGroupName(name);
        return threadPoolExecutor;
    }

    public static Map<String, CommonThreadPoolExecutor> getPoolExecutorMap() {
        return poolExecutorMap;
    }
}
