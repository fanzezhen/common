package com.github.fanzezhen.common.core.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author zezhen.fan
 */
public interface LockService {

    <T> T lockKey(String key, Supplier<T> supplier);

    <T> T lockKey(String key, long waitTime, TimeUnit timeUnit, Supplier<T> supplier);
}
