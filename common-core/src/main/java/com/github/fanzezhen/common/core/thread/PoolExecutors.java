package com.github.fanzezhen.common.core.thread;

import com.github.fanzezhen.common.core.property.CommonThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zezhen.fan
 */
@Slf4j
@SuppressWarnings("unused")
public class PoolExecutors {
    private static final Map<String, ThreadPoolExecutor> POOL_EXECUTOR_MAP = new ConcurrentHashMap<>(2);
    private static final Map<String, CommonThreadPoolTaskExecutor> POOL_TASK_EXECUTOR_MAP = new ConcurrentHashMap<>(2);
    static CommonThreadPoolProperties threadPoolProperties = new CommonThreadPoolProperties();

    private PoolExecutors() {
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name) {
        return newThreadPoolExecutor(name,
                threadPoolProperties.getCoreSize(),
                threadPoolProperties.getMaxSize(),
                threadPoolProperties.getKeepAliveSeconds(),
                threadPoolProperties.getQueueCapacity(),
                null,
                null
        );
    }

    public static ThreadPoolExecutor newThreadPoolCallerRunsPolicyExecutor(String name) {
        return newThreadPoolExecutor(name,
                threadPoolProperties.getCoreSize(),
                threadPoolProperties.getMaxSize(),
                threadPoolProperties.getKeepAliveSeconds(),
                threadPoolProperties.getQueueCapacity(),
                null,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name, RejectedExecutionHandler rejectedExecutionHandler) {
        return newThreadPoolExecutor(name,
                threadPoolProperties.getCoreSize(),
                threadPoolProperties.getMaxSize(),
                threadPoolProperties.getKeepAliveSeconds(),
                threadPoolProperties.getQueueCapacity(),
                null,
                rejectedExecutionHandler
        );
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name,
                                                           int coreSize,
                                                           int maxSize,
                                                           int keepAliveSeconds,
                                                           int queueCapacity,
                                                           ThreadFactory threadFactory,
                                                           RejectedExecutionHandler rejectedExecutionHandler) {
        return newThreadPoolExecutor(name, coreSize, maxSize, keepAliveSeconds, new LinkedBlockingQueue<>(queueCapacity), threadFactory, rejectedExecutionHandler);
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(String name,
                                                           int coreSize,
                                                           int maxSize,
                                                           int keepAliveSeconds,
                                                           BlockingQueue<Runnable> workQueue,
                                                           ThreadFactory threadFactory,
                                                           RejectedExecutionHandler rejectedExecutionHandler) {
        return POOL_EXECUTOR_MAP.computeIfAbsent(name, k -> {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    coreSize,
                    maxSize,
                    keepAliveSeconds,
                    TimeUnit.SECONDS,
                    workQueue,
                    threadFactory != null ? threadFactory : Executors.defaultThreadFactory()
            );
            if (rejectedExecutionHandler != null) {
                threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
            }
            return threadPoolExecutor;
        });
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name) {
        return newThreadPoolTaskExecutor(name, threadPoolProperties.getCoreSize());
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, RejectedExecutionHandler rejectedExecutionHandler) {
        return newThreadPoolTaskExecutor(name,
                threadPoolProperties.getCoreSize(),
                threadPoolProperties.getMaxSize(),
                threadPoolProperties.getQueueCapacity(),
                threadPoolProperties.getKeepAliveSeconds(),
                null,
                rejectedExecutionHandler
        );
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, ThreadFactory threadFactory) {
        return newThreadPoolTaskExecutor(name,
                threadPoolProperties.getCoreSize(),
                threadPoolProperties.getMaxSize(),
                threadPoolProperties.getQueueCapacity(),
                threadPoolProperties.getKeepAliveSeconds(),
                threadFactory,
                null
        );
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize) {
        return newThreadPoolTaskExecutor(name, coreSize, threadPoolProperties.getMaxSize());
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize) {
        return newThreadPoolTaskExecutor(name, coreSize, maxSize, threadPoolProperties.getQueueCapacity());
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity) {
        return newThreadPoolTaskExecutor(name, coreSize, maxSize, queueCapacity, threadPoolProperties.getKeepAliveSeconds());
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds) {
        return newThreadPoolTaskExecutor(name, coreSize, maxSize, queueCapacity, keepAliveSeconds, null, null);
    }

    public static CommonThreadPoolTaskExecutor newThreadPoolTaskExecutor(String name, int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds,
                                                                         ThreadFactory threadFactory,
                                                                         RejectedExecutionHandler rejectedExecutionHandler) {
        return POOL_TASK_EXECUTOR_MAP.computeIfAbsent(name, k -> {
            CommonThreadPoolTaskExecutor threadPoolExecutor = new CommonThreadPoolTaskExecutor();
            threadPoolExecutor.setCorePoolSize(coreSize);
            threadPoolExecutor.setMaxPoolSize(maxSize);
            threadPoolExecutor.setQueueCapacity(queueCapacity);
            threadPoolExecutor.setKeepAliveSeconds(keepAliveSeconds);
            threadPoolExecutor.setThreadGroupName(name);
            threadPoolExecutor.afterPropertiesSet();
            if (threadFactory != null) {
                threadPoolExecutor.setThreadFactory(threadFactory);
            }
            if (rejectedExecutionHandler != null) {
                threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
            }
            return threadPoolExecutor;
        });
    }

    public static Map<String, CommonThreadPoolTaskExecutor> getPoolTaskExecutorMap() {
        return POOL_TASK_EXECUTOR_MAP;
    }
}
