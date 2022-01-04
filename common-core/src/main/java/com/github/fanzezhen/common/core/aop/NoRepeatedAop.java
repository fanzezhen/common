package com.github.fanzezhen.common.core.aop;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.github.fanzezhen.common.core.annotion.NoRepeated;
import com.github.fanzezhen.common.core.service.CacheService;
import com.github.fanzezhen.common.core.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component("FzzNoRepeatedAop")
public class NoRepeatedAop {
    @Resource(name = "${com.github.fanzezhen.common.core.common.no-repeated.cache-service-bean:FzzCacheServiceImpl}")
    private CacheService cacheService;
    @Value("${spring.application.name:}")
    private String springApplicationName;
    /**
     * 环境分离变量
     */
    @Value("${spring.profiles.active:}")
    private String env;

    /**
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.common.core.annotion.NoRepeated)")
    public void cut() {
    }

    /**
     * 在调用上面 @Pointcut标注的方法前执行以下方法
     *
     * @param joinPoint JoinPoint
     */
    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {
        String key = null;
        String value = null;
        NoRepeated noRepeated = null;
        try {
            noRepeated = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoRepeated.class);
            if (noRepeated == null || !noRepeated.isTimeoutCheck()) {
                return;
            }
            String md5 = SecureUtil.md5(JSON.toJSONString(Arrays.stream(joinPoint.getArgs()).filter(arg -> !(arg instanceof HttpServletRequest)).collect(toList())));
            key = springApplicationName + env + "noRepeated-timeout：" + md5;
            value = cacheService.get(key);
        } catch (Throwable throwable) {
            log.warn("noRepeated check failed exception", throwable);
        }
        if (!StrUtil.isEmpty(value)) {
            ExceptionUtil.throwException("请勿重复提交");
        }
        try {
            if (noRepeated != null && noRepeated.isTimeoutCheck()) {
                cacheService.put(key, String.valueOf(System.currentTimeMillis()), noRepeated.timeoutMillis());
            }
        } catch (Throwable throwable) {
            log.warn("noRepeated put failed exception", throwable);
        }
    }

    /**
     * @param joinPoint 切点
     * @return ActionResult
     */
    @Around("cut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        String key = null;
        String value = null;
        NoRepeated noRepeated = null;
        try {
            noRepeated = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoRepeated.class);
            if (noRepeated != null && noRepeated.isTransactionLockCheck()) {
                String md5 = SecureUtil.md5(JSON.toJSONString(Arrays.stream(joinPoint.getArgs()).filter(arg ->
                        !(arg instanceof HttpServletRequest)).collect(toList())));
                key = springApplicationName + env + "noRepeated-transaction：" + md5;
                value = cacheService.get(key);
            }
        } catch (Throwable throwable) {
            log.warn("noRepeated check failed exception", throwable);
        }
        if (!StrUtil.isEmpty(value)) {
            ExceptionUtil.throwException("请勿重复提交");
        }
        try {
            if (noRepeated != null && noRepeated.isTransactionLockCheck()) {
                cacheService.put(key, String.valueOf(System.currentTimeMillis()), noRepeated.timeoutMillis());
            }
        } catch (Throwable throwable) {
            log.warn("noRepeated put failed exception", throwable);
        }
        try {
            result = joinPoint.proceed();
        } finally {
            try {
                if (key != null) {
                    cacheService.remove(key);
                }
            } catch (Throwable throwable) {
                log.warn("noRepeated TransactionLock freed failed exception", throwable);
            }
        }
        return result;
    }
}