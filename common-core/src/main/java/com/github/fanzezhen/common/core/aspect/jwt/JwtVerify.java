package com.github.fanzezhen.common.core.aspect.jwt;

import java.lang.annotation.*;

/**
 * JWT校验
 *
 * @author zezhen.fan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JwtVerify {
    String header() default "TM-Header-Base-JwtToken";
}
