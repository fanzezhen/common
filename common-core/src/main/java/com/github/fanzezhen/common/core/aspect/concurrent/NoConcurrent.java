package com.github.fanzezhen.common.core.aspect.concurrent;

import com.github.fanzezhen.common.core.constant.SysConstant;

import java.lang.annotation.*;

/**
 * 禁止并发
 *
 * @author zezhen.fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoConcurrent {

    String key() default "";
    String[] headerArgs() default {SysConstant.HEADER_TENANT_ID};
}
