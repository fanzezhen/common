package com.github.fanzezhen.common.core.annotion;

import java.lang.annotation.*;

/**
 * 防止重复提交注解
 *
 * @author zezhen.fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoRepeated {
    /**
     * 开启时间校验（timeoutSeconds时间段内判断重复点击）
     */
    boolean isTimeoutCheck() default true;

    /**
     * 开启“事务锁”校验（方法未结束且未超时则判断重复点击）
     */
    boolean isTransactionLockCheck() default false;

    /**
     * 超时时间（毫秒）
     */
    long timeoutMillis() default 1000L;
}
