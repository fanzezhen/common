package com.github.fanzezhen.common.core.annotion;

import com.github.fanzezhen.common.core.constant.SysConstant;
import com.github.fanzezhen.common.core.enums.NoRepeatTypeEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 防止重复提交注解
 *
 * @author zezhen.fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoRepeat {
    /**
     * 开启时间校验（timeoutSeconds时间段内判断重复点击）
     */
    boolean isTimeoutCheck() default true;

    /**
     * 开启“事务锁”校验（方法未结束且未超时则判断重复点击）
     */
    boolean isTransactionLockCheck() default false;

    NoRepeatTypeEnum type() default NoRepeatTypeEnum.TIME;

    String[] paramArgs() default {};
    String[] headerArgs() default {SysConstant.HEADER_TENANT_ID};

    TimeUnit timeUnit() default TimeUnit.SECONDS;
    /**
     * 超时时间（毫秒）
     */
    long timeout() default 1;
}
